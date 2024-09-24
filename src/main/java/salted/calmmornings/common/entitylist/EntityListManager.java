package salted.calmmornings.common.entitylist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.ModList;
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
        boolean listType;
        if (listEnabled) {
            listType = isBlackList;
        } else {
            listType = true;
        }
        Set<ResourceLocation> names = BuiltInRegistries.ENTITY_TYPE.keySet();
        HashSet<String> list = listEnabled ? mobList: defaultBlackList;
        ThreadManager manager = new ThreadManager();

        for (ResourceLocation resource : names) {
            Runnable task = () -> {
                String entityId = resource.toString();
                EntityType.byString(entityId).ifPresent(entity -> {
                    ListBuilder.addEntity(entityId, entity, listType);
                    CalmMornings.LOGGER.debug("Adding [{}] to map", entityId);
                });
            };
            manager.addTask(task);
        }

        oldListType = listType;
        manager.shutdown();
        manager.restart(5);

        // add bedbugs to the default blacklist if sleep tight is loaded
        if (ModList.get().isLoaded("sleep_tight")) defaultBlackList.add("sleep_tight:bedbug");
        ListBuilder.updateFilterList();

        wasListEnabled = listEnabled;
        oldListType = isBlackList;

        updateMobList(list, listEnabled, isBlackList, manager);
        updateCategoryList(categoryList, manager);
        manager.shutdown();
        manager.awaitShutdown(5);
    }

    public static void updateMobList(HashSet<String> newMobList, boolean listEnabled, boolean isBlackList, ThreadManager manager) {
        HashSet<String> list = listEnabled ? new HashSet<>(newMobList) : new HashSet<>(defaultBlackList);
        CalmMornings.LOGGER.debug("List Enabled: {} using {}", listEnabled, list);

        // compare old list against incoming list
        Sets.SetView<String> deleted = Sets.difference(oldMobList, list);
        CalmMornings.LOGGER.debug("Deleted category list {}", deleted);
        Sets.SetView<String> added = Sets.difference(list, oldMobList);
        CalmMornings.LOGGER.debug("Added category list {}", added);

        boolean newListType;
        if (listEnabled) {
            CalmMornings.LOGGER.debug("List Enabled. isBlackList = {}", isBlackList);
            newListType = isBlackList;
        } else {
            CalmMornings.LOGGER.debug("List disabled. isBlackList = {}", true);
            newListType = true;
        }

        boolean addValue;
        boolean deleteValue;
        if (newListType) { addValue = false; deleteValue = true; } else { addValue = true; deleteValue = false; }
        if (oldListType != newListType) ListBuilder.flipAllValues();

        // set all removed entries to false
        deleted.forEach(entityId -> setEntityValues(entityId, deleteValue, manager));
        // set all changed entries to true
        added.forEach(entityId -> setEntityValues(entityId, addValue, manager));

        oldListType = newListType;
        oldMobList = list;
        CalmMornings.LOGGER.debug("Old List and mob list updated {} {}", oldListType, oldMobList);
    }

    public static void updateCategoryList(HashSet<String> newCategoryList, ThreadManager manager) {
        HashSet<String> list = new HashSet<>(newCategoryList);
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

    private static boolean oldListCheck(boolean listEnabled, boolean isBlackList) {
        CalmMornings.LOGGER.debug("Values of [wasListEnabled, listEnabled, isBlackList]: [{}, {}, {}]", wasListEnabled, listEnabled, isBlackList);
        if (wasListEnabled && listEnabled && isBlackList) return false;
        if (wasListEnabled && listEnabled) return true;
        else return listEnabled && isBlackList;
    }

    // entity despawn value methods
    private static void setEntityValues(String entity, boolean added, ThreadManager manager) {
        Optional<Pair<String, String>> optional = ListBuilder.entityKey(entity);
        if (optional.isEmpty()) return;
        Pair<String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getRight();

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

        if (entityId.equals("*")) setAllCategories(modId, mobCategory, added, manager);
        else if (added) ListBuilder.updateEntityCategory(key);
        else setDefaultCategory(modId, entityId);
    }

    private static void setAllCategories(String modId, String mobCategory, boolean added, ThreadManager manager) {
        ImmutableMap<String, ListInfo> entityIdMap = ListBuilder.getEntityIdMap(modId);

        // construct keys from all entity ids in map and change mobCategory
        for (String entityId: entityIdMap.keySet()) {
            Runnable task = () -> {
                if (added) ListBuilder.updateEntityCategory(Triple.of(modId, entityId, mobCategory));
                else setDefaultCategory(modId, entityId);
            };
            manager.addTask(task);
        }
    }

    private static void setDefaultCategory(String modId, String entityId) {
        ResourceLocation resourceKey = ResourceLocation.fromNamespaceAndPath(modId, entityId);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(resourceKey);

        MobCategory category = type.getCategory();
        String mobCategory = category.getName();

        Triple<String, String, String> key = Triple.of(modId, entityId, mobCategory);
        ListBuilder.updateEntityCategory(key);
    }

}
