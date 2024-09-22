package salted.calmmornings.common.utils;


import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import salted.calmmornings.common.Config;




public final class MobListUtils {

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

    private static final HashMap<String, HashMap<String, EntityDetails>> entityMap = new HashMap<>();

    public static HashMap<String, HashMap<String, EntityDetails>> getEntityMap() { return entityMap; }

    public static void addEntity(@NotNull String entity_name, EntityType<?> type) {
        // Split the entity name from the item type
        String[] split = entity_name.split(":");
        String modId = split[0];
        String entityId = split[1];
        // Get hashmap
        HashMap<String, HashMap<String, EntityDetails>> map = getEntityMap();
        HashMap<String, EntityDetails> inner_map = new HashMap<>();
        // Check if we're running the default or if the config is enabled
        if (Config.ENABLE_LIST.get()) {
            inner_map.put(entityId, new EntityDetails(type.getCategory(), Config.IS_BLACKLIST.get()));
        } else {
            inner_map.put(entityId, new EntityDetails(type.getCategory(), true));
        }
        // Get the mod map if it exists. If it doesn't create
        if (map.containsKey(modId)) {
            map.get(modId).putAll(inner_map);
        } else {
            map.put(modId, inner_map);
        }
    }

    public static void hydrateEntities(boolean isDefault) {
        HashMap<String, HashMap<String, EntityDetails>> map = getEntityMap();
        if (isDefault) {
            setAllEntities(true);
            for (String entity : blackList) {
                setMobDespawnValue(entity.split(":"), false, map);
            }
        } else {
            List<? extends String> list = Config.MOB_LIST.get();
            boolean isBlackList = Config.IS_BLACKLIST.get();
            setAllEntities(isBlackList);
            for (String entity : list) {
                String[] split = entity.split(":");
                if (split.length == 1) {
                    setAllInModID(split[0], !isBlackList, map);
                } else {
                    setMobDespawnValue(split, !isBlackList, map);
                }
            }
        }
    }

    private static void setMobDespawnValue(String[] mobInf, boolean value, HashMap<String, HashMap<String, EntityDetails>> map) {
        String modId = mobInf[0];
        String entityId = mobInf[1];
        map.get(modId).get(entityId).setDespawnable(value);
    }

    private static void setAllInModID(String modId, boolean value, HashMap<String, HashMap<String, EntityDetails>> map) {
        map.get(modId).forEach((entityId, entityDetails) -> entityDetails.setDespawnable(value));
    }

    private static void setAllEntities(boolean value) {
        HashMap<String, HashMap<String, EntityDetails>> entityMap = getEntityMap();
        entityMap.forEach((modId, innerMap) -> { innerMap.forEach((entityId, entityDetails) -> entityDetails.setDespawnable(value));});
    }
}
