package salted.calmmornings.common.entitylist;


import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public final class ListBuilder {
    public static List<String> getBlackList() { return blackList; }

    public static ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> getEntityMap() { return entityMap; }

    public static void addEntity(@NotNull String entity, EntityType<?> type) {
        // get modId and entityId if they exist
        Optional<Tuple<String, String>> optional = entityKey(entity);
        if (optional.isEmpty()) return;
        Tuple<String, String> key = optional.get();

        String modId = key.getA();
        String entityId = key.getB();
        // get hashmap
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = getEntityMap();
        ConcurrentHashMap<String, ListInfo> inner_map = new ConcurrentHashMap<>();

        // check if list is enabled else use default values
        if (Config.ENABLE_LIST.get()) inner_map.put(entityId, new ListInfo(type.getCategory(), Config.IS_BLACKLIST.get()));
        else inner_map.put(entityId, new ListInfo(type.getCategory(), true));

        // get the mod map if it exists, else create map
        if (map.containsKey(modId)) map.get(modId).putAll(inner_map);
        else map.put(modId, inner_map);
    }

    public static void hydrateEntities(boolean isDefault) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = getEntityMap();
        if (isDefault) { // use mobCategory if list is not enabled
            setAllEntities(true);

            for (String entity : blackList) {
                Optional<Tuple<String, String>> optional = entityKey(entity);
                if (optional.isEmpty()) return;
                Tuple<String, String> key = optional.get();

                setDespawnValue(key, false, map);
            }
        } else { // set which entities can despawn based on list
            List<? extends String> list = Config.MOB_LIST.get();
            boolean isBlackList = Config.IS_BLACKLIST.get();
            setAllEntities(isBlackList);

            for (String entity : list) {
                Optional<Tuple<String, String>> optional = entityKey(entity);
                if (optional.isEmpty()) return;

                Tuple<String, String> key = optional.get();
                String modId = key.getA();
                String entityId = key.getB();

                /* set value for all entities in modId if value in list equals "<modId>:*"
                else set value for each individual entity in list */
                if (entityId.equals("*")) setAllInModID(modId, !isBlackList, map);
                else setDespawnValue(key, !isBlackList, map);
            }
        }
    }

    public static Optional<Tuple<String, String>> entityKey(String entity) {
        String[] key = entity.split(":");

        if (key.length == 1) return Optional.empty();
        return Optional.of(new Tuple<>(key[0], key[1]));
    }

    // private methods for determining values/conditions
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> entityMap = new ConcurrentHashMap<>();

    private static final ArrayList<String> blackList = new ArrayList<>(List.of(
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

    private static void setDespawnValue(Tuple<String, String> key, boolean value, ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map) {
        String modId = key.getA();
        if (!map.containsKey(modId)) return;

        ConcurrentHashMap<String, ListInfo> innerMap = map.get(modId);
        String entityId = key.getB();

        if (innerMap.containsKey(entityId)) innerMap.get(entityId).setDespawnable(value);
    }

    private static void setAllInModID(String modId, boolean value, ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map) {
        if (!map.containsKey(modId)) return;
        map.get(modId).forEach((entityId, listInfo) -> listInfo.setDespawnable(value));
    }

    private static void setAllEntities(boolean value) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> entityMap = getEntityMap();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        entityMap.forEach((modId, innerMap) -> {
            Runnable runnable = () -> setAllInModID(modId, value, entityMap);
            pool.execute(runnable);
        });
        pool.shutdown();
        try {
            if(pool.awaitTermination(7, TimeUnit.SECONDS)) { CalmMornings.LOGGER.debug("Thread pool successfully terminated"); }
        } catch (InterruptedException e) {
            CalmMornings.LOGGER.debug("Failed to shutdown the thread pool in a timely manner");
        }
    }
}
