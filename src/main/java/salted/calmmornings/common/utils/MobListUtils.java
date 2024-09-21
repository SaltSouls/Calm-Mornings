package salted.calmmornings.common.utils;


import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import salted.calmmornings.common.Config;




public final class MobListUtils {

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
            inner_map.put(entityId, new EntityDetails(type.getCategory(), Config.IS_BLACKLIST));
        }
        // Get the mod map if it exists. If it doesn't create
        if (map.containsKey(modId)) {
            map.get(modId).putAll(inner_map);
        } else {
            map.put(modId, inner_map);
        }
    }

    public static void hydrateEntities() {
        boolean listEnabled = Config.ENABLE_LIST.get();
        if (listEnabled) {
            boolean isBlackList = Config.IS_BLACKLIST.get();
            setAllEntities(isBlackList);
        } else {
            List<? extends String> list = Config.MOB_LIST.get();

        }
    }

    private static void setAllEntities(boolean value) {
        HashMap<String, HashMap<String, EntityDetails>> entityMap = getEntityMap();
        entityMap.forEach((modId, innerMap) -> { innerMap.forEach((entityId, entityDetails) -> entityDetails.setDespawnable(value));});
    }
}
