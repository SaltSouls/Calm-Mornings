package salted.calmmornings.common.entitylist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class EntityListManager {

    // private methods for determining values/conditions
    private static HashSet<String> lastMobList = new HashSet<>();
    private static HashSet<String> lastCategoryList = new HashSet<>();
    private static boolean lastIsBlackList;
    private static final HashSet<String> defaultBlackList = new HashSet<>(Arrays.asList(
            // bosses/dungeon enemies
            "minecraft:ender_dragon",
            "minecraft:wither",
            "minecraft:guardian",
            "minecraft:elder_guardian",
            /* this should prevent raids/roaming parties from being
              affected, though there might be a better way to do this */
            "minecraft:pillager",
            "minecraft:evoker",
            "minecraft:illusioner",
            "minecraft:ravager",
            // this shouldn't happen, but better safe than sorry
            "minecraft:player"
    ));

    // private methods for determining values/conditions
    private static void sleeptightCompat() {
        defaultBlackList.add("sleep_tight:bedbug");
    }

    public static void initializeMap(HashSet<String> newMobList, HashSet<String> newCategoryList, boolean listEnabled, boolean isBlackList) {
        Set<ResourceLocation> names = BuiltInRegistries.ENTITY_TYPE.keySet();
        ThreadManager manager = new ThreadManager();

        HashSet<String> list = listEnabled ? newMobList: defaultBlackList;

        for (ResourceLocation resource : names) {
            Runnable task = () -> {
                String entityId = resource.toString();
                EntityType.byString(entityId).ifPresent(entity -> {
                    ListBuilder.addEntity(entityId, entity, isBlackList);
                    CalmMornings.LOGGER.debug("Adding [{}] to map", entityId);
                });
            };
            manager.addTask(task);
        }
        manager.shutdown();
        manager.awaitShutdown(5);

        if (ModList.get().isLoaded("sleep_tight")) sleeptightCompat();
        ListBuilder.updateFilterList();

        lastIsBlackList = isBlackList;
        updateMobList(list, listEnabled, isBlackList);
        updateCategoryList(newCategoryList);
    }

    public static void updateMobList(HashSet<String> newMobList, boolean listEnabled, boolean isBlackList) {
        CalmMornings.LOGGER.debug("New Mob List {}\nOld Mob List {}", newMobList, lastMobList);
        HashSet<String> list = listEnabled ? newMobList : defaultBlackList;

        Sets.SetView<String> deleted = Sets.difference(lastMobList, list);
        Sets.SetView<String> added = Sets.difference(list, lastMobList);
        CalmMornings.LOGGER.debug("added list {}", added);
        CalmMornings.LOGGER.debug("deleted list {}", deleted);
        
        boolean newIsBlackList = !listEnabled || isBlackList;
        CalmMornings.LOGGER.debug("Is BlackList: {}", newIsBlackList);
        if (lastIsBlackList != newIsBlackList) flipAllValues();

        // set all removed entries to false
        deleted.forEach(entityId -> setEntityValues(entityId, newIsBlackList));
        // set all changed entries to true
        added.forEach(entityId -> setEntityValues(entityId, !newIsBlackList));

        lastIsBlackList = newIsBlackList;
        lastMobList = new HashSet<>(newMobList);
    }

    public static void updateCategoryList(HashSet<String> newCategoryList) {
        CalmMornings.LOGGER.debug("New CategoryList List {}\nOld Category List {}", newCategoryList, lastCategoryList);
        Sets.SetView<String> deleted = Sets.difference(lastCategoryList, newCategoryList);
        Sets.SetView<String> added = Sets.difference(newCategoryList, lastCategoryList);
        CalmMornings.LOGGER.debug("added category list {}", added);
        CalmMornings.LOGGER.debug("deleted category list {}", deleted);

        // set all removed entries to false
        deleted.forEach(entityId -> setCategoryValues(entityId, false));
        // set all changed entries categories
        added.forEach(entityId -> setCategoryValues(entityId, true));

        lastCategoryList = new HashSet<>(newCategoryList);
    }

    private static void setEntityValues(String entity, boolean added) {
        CalmMornings.LOGGER.debug("Setting Entity Value for {} to {}", entity, added);
        Optional<Pair<String, String>> optional = ListBuilder.entityKey(entity);
        if (optional.isEmpty()) return;
        Pair<String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getRight();

        if (entityId.equals("*")) updateAllValues(modId, added);
        else ListBuilder.updateEntity(key, added, ListBuilder.getEntityMap());
    }

    private static void setCategoryValues(String entity, boolean added) {
        Optional<Triple<String, String, String>> optional = ListBuilder.categoryKey(entity);
        if (optional.isEmpty()) return;
        Triple<String, String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getMiddle();
        String mobCategory = key.getRight();

        if (entityId.equals("*")) updateAllCategory(modId, mobCategory, added);
        else if (added) ListBuilder.updateEntityCategory(key);
        else setDefaultCategory(modId, entityId);
    }

    private static void updateAllCategory(String modId, String mobCategory, boolean added) {
        ImmutableMap<String, ListInfo> map = ListBuilder.getImmutableMap(modId);
        ThreadManager manager = new ThreadManager();

        for (String entityId: map.keySet()) {
            Runnable task = () -> {
                Triple<String, String, String> key = Triple.of(modId, entityId, mobCategory);

                if (added) ListBuilder.updateEntityCategory(key);
                else setDefaultCategory(modId, entityId);
            };
            manager.addTask(task);
        }
        manager.shutdown();
        manager.awaitShutdown(5);
    }

    // returns all entities contained in a specific modId
    private static void updateAllValues(String modId, boolean added) {
        ImmutableMap<String, ListInfo> map = ListBuilder.getImmutableMap(modId);
        ThreadManager manager = new ThreadManager();

        for (String entityId: map.keySet()) {
            Runnable task = () -> ListBuilder.updateEntity(Pair.of(modId, entityId), added, ListBuilder.getEntityMap());
            manager.addTask(task);
        }
        manager.shutdown();
        manager.awaitShutdown(5);
    }

    private static void flipAllValues() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = ListBuilder.getEntityMap();
        map.forEach((modId, innerMap) -> innerMap.forEach((entityId, listInfo) -> listInfo.setDespawnable(!listInfo.getDespawnable())));
    }

    private static void setDefaultCategory(String modId, String entityId) {
        ResourceLocation resourceKey = ResourceLocation.fromNamespaceAndPath(modId, entityId);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(resourceKey);

        MobCategory category = type.getCategory();
        String mobCategory = category.getName().toUpperCase();

        Triple<String, String, String> key = Triple.of(modId, entityId, mobCategory);
        ListBuilder.updateEntityCategory(key);
    }

}
