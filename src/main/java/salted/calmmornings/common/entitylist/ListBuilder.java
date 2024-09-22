package salted.calmmornings.common.entitylist;

import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.threading.ThreadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
        ConcurrentHashMap<String, ListInfo> innerMap = new ConcurrentHashMap<>();

        // check if list is enabled else use default values
        if (Config.ENABLE_LIST.get()) innerMap.put(entityId, new ListInfo(type.getCategory(), Config.IS_BLACKLIST.get()));
        else innerMap.put(entityId, new ListInfo(type.getCategory(), true));

        // get the mod map if it exists, else create map
        if (map.containsKey(modId)) map.get(modId).putAll(innerMap);
        else map.put(modId, innerMap);
    }

    public static void configureEntities(boolean listEnabled) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = getEntityMap();
        ThreadManager manger = new ThreadManager();

        if (!listEnabled) { // use mobCategory if list is not enabled
            setAllEntities(true);

            for (String entity : blackList) {
                Runnable task = () -> {
                    Optional<Tuple<String, String>> optional = entityKey(entity);
                    if (optional.isEmpty()) return;
                    Tuple<String, String> key = optional.get();

                    setDespawnValue(key, false, map);
                };
                manger.addTask(task);
            }
        } else { // set which entities can despawn based on list
            List<? extends String> list = Config.MOB_LIST.get();
            boolean isBlackList = Config.IS_BLACKLIST.get();
            setAllEntities(isBlackList);

            for (String entity : list) {
                Runnable task = () -> {
                    Optional<Tuple<String, String>> optional = entityKey(entity);
                    if (optional.isEmpty()) return;

                    Tuple<String, String> key = optional.get();
                    String modId = key.getA();
                    String entityId = key.getB();

                    /* set value for all entities in modId if value in list equals "<modId>:*"
                    else set value for each individual entity in list */
                    if (entityId.equals("*")) {
                        CalmMornings.LOGGER.info("Configuring all [{}", modId + "] entities");
                        setAllInModID(modId, !isBlackList, map);
                    }
                    else {
                        setDespawnValue(key, !isBlackList, map);
                    }
                };
                manger.addTask(task);
            }
        }
        manger.shutdown();
        manger.awaitShutdown(5);
    }

    public static Optional<Tuple<String, String>> entityKey(String entity) {
        String[] key = entity.split(":");

        // get the modId(key[0]) and entityId(key[1]) from entityKey
        if (key.length == 1) {
            CalmMornings.LOGGER.error("[{}", key[0] + "] is not a valid list entry!");
            return Optional.empty();
        }
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
        if (!map.containsKey(modId)) {
            CalmMornings.LOGGER.error("modId [{}", modId + "] is not in map!");
            return;
        }

        ConcurrentHashMap<String, ListInfo> innerMap = map.get(modId);
        String entityId = key.getB();

        if (innerMap.containsKey(entityId)) {
            CalmMornings.LOGGER.info("Configured: [" + modId + ":" + entityId + "]");
            innerMap.get(entityId).setDespawnable(value);
        }
        else CalmMornings.LOGGER.error("[" + entityId + "] does not exist in [" + modId + "]!");
    }

    private static void setAllInModID(String modId, boolean value, ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map) {
        if (!map.containsKey(modId)) return;
        ThreadManager manger = new ThreadManager();

        map.get(modId).forEach((entityId, listInfo) -> {
            Runnable task = () -> {
                CalmMornings.LOGGER.debug("Configured: [" + modId + ":" + entityId + "]");
                listInfo.setDespawnable(value);
            };
            manger.addTask(task);
        });
        manger.shutdown();
        manger.awaitShutdown(5);
    }

    private static void setAllEntities(boolean value) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> entityMap = getEntityMap();
        ThreadManager manger = new ThreadManager();

        entityMap.forEach((modId, innerMap) -> {
            CalmMornings.LOGGER.debug("Defaulting all [{}", modId + "] entities:");
            Runnable task = () -> setAllInModID(modId, value, entityMap);
            manger.addTask(task);
        });
        manger.shutdown();
        manger.awaitShutdown(5);
    }
}
