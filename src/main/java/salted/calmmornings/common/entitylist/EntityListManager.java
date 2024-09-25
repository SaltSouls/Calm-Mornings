package salted.calmmornings.common.entitylist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.threading.ThreadManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityListManager {

    public static void initMap(HashSet<String> mobList, HashSet<String> categoryList, boolean listEnabled, boolean isBlackList) {
        Set<ResourceLocation> names = ForgeRegistries.ENTITIES.getKeys();
        HashSet<String> list = listEnabled ? mobList: defaultBlackList;
        ThreadManager manager = new ThreadManager();
        boolean listType;

        // check if the list is enabled and if list is a blacklist
        CalmMornings.LOGGER.debug("listEnabled: {}", listEnabled);
        CalmMornings.LOGGER.debug("isBlackList: {}", isBlackList);
        if (listEnabled) listType = isBlackList;
        else listType = true;

        HashSet<String> modIds = new HashSet<>();

        // build initial map based on passed in parameters
        for (ResourceLocation resource : names) {
            String modId = resource.getNamespace();

            // check if modId has already been added to the map
            if (!modIds.contains(modId)) {
                CalmMornings.LOGGER.debug("New modId [{}] added to map", modId);
                ListBuilder.addModIdToMap(modId);
                modIds.add(modId);
            }

            Runnable task = () -> {
                String entityKey = resource.toString();
                String entityId = resource.getPath();

                // add entities to map
                EntityType.byString(entityKey).ifPresent(entity -> {
                    ListBuilder.addEntity(modId, entityId, entity, listType);
                    CalmMornings.LOGGER.debug("Adding [{}] to map", entityId);
                });
            };
            manager.addTask(task);
        }
        manager.shutdown();
        manager.restart(5);

        names.forEach(name -> {
            ImmutableMap<String, ListInfo> map = ListBuilder.getEntityIdMap(name.getNamespace());
            if (map.containsKey(name.getPath())) CalmMornings.LOGGER.debug("Found [{}] in map", name.getPath());
            else CalmMornings.LOGGER.error("[{}] missing from map", name.getPath());
        });

        ListBuilder.updateFilterList();
        oldListType = listType;

        // update initialized map if needed
        updateMobList(list, listEnabled, isBlackList, manager);
        updateCategoryList(categoryList, manager);
        manager.shutdown();
        manager.awaitShutdown(5);
    }

    public static void updateMobList(HashSet<String> newMobList, boolean listEnabled, boolean isBlackList, ThreadManager manager) {
        HashSet<String> list = listEnabled ? new HashSet<>(newMobList) : new HashSet<>(defaultBlackList);
        boolean newListType;

        // compare old list against incoming list
        Sets.SetView<String> deleted = Sets.difference(oldMobList, list);
        CalmMornings.LOGGER.debug("Deleted category list {}", deleted);
        Sets.SetView<String> added = Sets.difference(list, oldMobList);
        CalmMornings.LOGGER.debug("Added category list {}", added);

        // check if the list is enabled and if list is a blacklist
        CalmMornings.LOGGER.debug("listEnabled: {}", listEnabled);
        CalmMornings.LOGGER.debug("isBlackList: {}", isBlackList);
        if (listEnabled) newListType = isBlackList;
        else newListType = true;

        // get if the mob should despawn or not
        Pair<Boolean, Boolean> shouldDespawn;
        if (newListType) shouldDespawn = Pair.of(false, true);
        else shouldDespawn = Pair.of(true, false);

        // flip all values if the new and old list are not the same
        if (oldListType != newListType) ListBuilder.flipAllValues();

        // set all removed entries to false
        deleted.forEach(entityId -> setEntityValues(entityId, shouldDespawn.getRight(), manager));
        // set all changed entries to true
        added.forEach(entityId -> setEntityValues(entityId, shouldDespawn.getLeft(), manager));

        oldMobList = list;
        oldListType = newListType;
    }

    public static void updateCategoryList(HashSet<String> newCategoryList, ThreadManager manager) {
        HashSet<String> list = new HashSet<>(newCategoryList);

        // compare old list against incoming list
        Sets.SetView<String> deleted = Sets.difference(oldCategoryList, newCategoryList);
        CalmMornings.LOGGER.debug("Deleted category list {}", deleted);
        Sets.SetView<String> added = Sets.difference(newCategoryList, oldCategoryList);
        CalmMornings.LOGGER.debug("Added category list {}", added);

        // set all removed entries to false
        deleted.forEach(entityId -> setCategories(entityId, false, manager));
        // set all changed entries categories
        added.forEach(entityId -> setCategories(entityId, true, manager));

        oldCategoryList = list;
    }

    // private methods for determining values/conditions
    private static HashSet<String> oldMobList = new HashSet<>();
    private static HashSet<String> oldCategoryList = new HashSet<>();
    private static boolean oldListType;
    private static boolean wasListEnabled;
    private static final HashSet<String> defaultBlackList = new HashSet<>(Arrays.asList(
            // bosses/dungeon enemies
            "minecraft:ender_dragon",
            "minecraft:wither",
            "minecraft:warden",
            "minecraft:guardian",
            "minecraft:elder_guardian",
            /* this should prevent raids/roaming parties from being
             * affected, though there might be a better way to do this */
            "minecraft:pillager",
            "minecraft:evoker",
            "minecraft:illusioner",
            "minecraft:ravager",
            // this shouldn't happen, but better safe than sorry
            "minecraft:player"
    ));

    // entity despawn value methods
    private static void setEntityValues(String entity, boolean added, ThreadManager manager) {
        Optional<Pair<String, String>> optional = ListBuilder.entityKey(entity);
        if (optional.isEmpty()) return;
        Pair<String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getRight();

        // update all entities in modId if glob else update individual entity
        if (entityId.equals("*")) setAllValues(modId, added, manager);
        else ListBuilder.updateEntity(key, added);
    }

    private static void setAllValues(String modId, boolean added, ThreadManager manager) {
        ImmutableMap<String, ListInfo> entityIdMap = ListBuilder.getEntityIdMap(modId);

        // construct keys from all entity ids in map and change value
        for (String entityId: entityIdMap.keySet()) {
            Runnable task = () -> ListBuilder.updateEntity(Pair.of(modId, entityId), added);
            manager.addTask(task);
        }
    }

    // entity category methods
    private static void setCategories(String entity, boolean added, ThreadManager manager) {
        Optional<Triple<String, String, String>> optional = ListBuilder.categoryKey(entity);
        if (optional.isEmpty()) return;
        Triple<String, String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getMiddle();
        String mobCategory = key.getRight();

        ResourceLocation entityKey = new ResourceLocation(modId, entityId);

        // update all entities in modId if glob else update individual entity
        if (entityId.equals("*")) setAllCategories(modId, mobCategory, added, manager);
        else if (added) ListBuilder.updateEntityCategory(key);
        else setDefaultCategory(entityKey);
    }

    private static void setAllCategories(String modId, String mobCategory, boolean added, ThreadManager manager) {
        ImmutableMap<String, ListInfo> entityIdMap = ListBuilder.getEntityIdMap(modId);

        // construct keys from all entity ids in map and change mobCategory
        for (String entityId: entityIdMap.keySet()) {
            Runnable task = () -> {
                ResourceLocation entity = new ResourceLocation(modId, entityId);
                if (added) ListBuilder.updateEntityCategory(Triple.of(modId, entityId, mobCategory));
                else setDefaultCategory(entity);
            };
            manager.addTask(task);
        }
    }

    private static void setDefaultCategory(ResourceLocation entity) {
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entity);
        String modId = type.getRegistryName().getNamespace();
        String entityId = type.getRegistryName().getPath();
        MobCategory category = type.getCategory();
        String mobCategory = category.getName();

        // reset entity category to its internally registered category
        Triple<String, String, String> key = Triple.of(modId, entityId, mobCategory);
        ListBuilder.updateEntityCategory(key);
    }

}
