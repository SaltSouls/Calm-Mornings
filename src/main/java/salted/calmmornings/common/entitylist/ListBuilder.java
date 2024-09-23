package salted.calmmornings.common.entitylist;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ListBuilder {
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> getEntityMap() { return entityMap; }
    public static HashSet<MobCategory> getFilterList() { return filterList; }

    public static ImmutableMap<String, ListInfo> getImmutableMap(String modId) {
        return ImmutableMap.copyOf(getEntityMap().get(modId));
    }

    public static void addEntity(@NotNull String entity, EntityType<?> type, boolean isBlackList) {
        // get modId and entityId if they exist
        Optional<Pair<String, String>> optional = entityKey(entity);
        if (optional.isEmpty()) return;
        Pair<String, String> key = optional.get();

        String modId = key.getLeft();
        String entityId = key.getRight();
        // get hashmap
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = getEntityMap();
        ConcurrentHashMap<String, ListInfo> innerMap = new ConcurrentHashMap<>();

        // check if list is enabled else use default values
        if (Config.ENABLE_LIST.get()) innerMap.put(entityId, new ListInfo(type.getCategory(), isBlackList));
        else innerMap.put(entityId, new ListInfo(type.getCategory(), true));

        // get the mod map if it exists, else create map
        if (map.containsKey(modId)) map.get(modId).putAll(innerMap);
        else map.put(modId, innerMap);
    }

    public static void updateFilterList() {
        HashSet<MobCategory> filterList = getFilterList();
        if (Config.MONSTER.getAsBoolean()) { filterList.add(MobCategory.MONSTER); }
        else { filterList.remove(MobCategory.MONSTER); }
        if (Config.CREATURE.getAsBoolean()) { filterList.add(MobCategory.CREATURE); }
        else { filterList.remove(MobCategory.CREATURE); }
        if (Config.AXOLOTLS.getAsBoolean()) { filterList.add(MobCategory.AXOLOTLS); }
        else { filterList.remove(MobCategory.AXOLOTLS); }
        if (Config.WATER_CREATURE.getAsBoolean()) { filterList.add(MobCategory.WATER_CREATURE); }
        else { filterList.remove(MobCategory.WATER_CREATURE); }
        if (Config.UNDERGROUND_WATER_CREATURE.getAsBoolean()) { filterList.add(MobCategory.UNDERGROUND_WATER_CREATURE); }
        else { filterList.remove(MobCategory.UNDERGROUND_WATER_CREATURE); }
        if (Config.AMBIENT.getAsBoolean()) { filterList.add(MobCategory.AMBIENT); }
        else { filterList.remove(MobCategory.AMBIENT); }
        if (Config.WATER_AMBIENT.getAsBoolean()) { filterList.add(MobCategory.WATER_AMBIENT); }
        else { filterList.remove(MobCategory.WATER_AMBIENT); }
        if (Config.MISC.getAsBoolean()) { filterList.add(MobCategory.MISC); }
        else { filterList.remove(MobCategory.MISC); }
    }

    public static Optional<Pair<String, String>> entityKey(String entity) {
        String[] key = entity.split(":");

        // get the modId(key[0]) and entityId(key[1]) from entityKey
        if (key.length == 1) {
            CalmMornings.LOGGER.error("[{}] is not a valid list entry!", key[0]);
            return Optional.empty();
        }
        return Optional.of(Pair.of(key[0], key[1]));
    }

    public static Optional<Triple<String, String, String>> categoryKey(String entity) {
        String[] split = entity.split(":");
        if (split.length < 3) return Optional.empty();
        return Optional.of(Triple.of(split[0], split[1], split[2]));
    }

    // private methods for determining values/conditions
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> entityMap = new ConcurrentHashMap<>();
    private static final HashSet<MobCategory> filterList = new HashSet<>();

    public static void updateEntity(Pair<String, String> key, boolean value, ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map) {
        String modId = key.getLeft();
        if (modIdError(modId, map)) return;

        ConcurrentHashMap<String, ListInfo> innerMap = map.get(modId);
        String entityId = key.getRight();
        if (entityIdError(entityId, innerMap)) return;

        CalmMornings.LOGGER.info("Configured: [{}:{}] to {}", modId, entityId, value);
        innerMap.get(entityId).setDespawnable(value);
    }

    public static void updateEntityCategory(Triple<String, String, String> key) {
        String modId = key.getLeft();
        String entityId = key.getMiddle();
        String category = key.getRight();
        
        // check if MobCategory actually exists
        Optional<MobCategory> categoryOpt = isValidCategory(category);
        if (categoryOpt.isEmpty()) {
            CalmMornings.LOGGER.error("MobCategory: [{}] does not exist!", category);
            return;
        }
        MobCategory mobCategory = categoryOpt.get();

        CalmMornings.LOGGER.info("Setting mobCategory of [{}:{}] to {}", modId, entityId, mobCategory.getName().toUpperCase());
        updateMobCategory(modId, entityId, mobCategory);
    }

    private static void updateMobCategory(String modId, String entityId, MobCategory category) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = getEntityMap();
        if (modIdError(modId, map)) return;
        ConcurrentHashMap<String, ListInfo> innerMap = map.get(modId);
        if (innerMap.containsKey(entityId)) innerMap.get(entityId).setCategory(category);

    }

    private static Optional<MobCategory> isValidCategory(String category) {
        return switch (category) {
            case "MONSTER" -> Optional.of(MobCategory.MONSTER);
            case "CREATURE" -> Optional.of(MobCategory.CREATURE);
            case "WATER_CREATURE" -> Optional.of(MobCategory.WATER_CREATURE);
            case "UNDERGROUND_WATER_CREATURE" -> Optional.of(MobCategory.UNDERGROUND_WATER_CREATURE);
            case "AMBIENT" -> Optional.of(MobCategory.AMBIENT);
            case "WATER_AMBIENT" -> Optional.of(MobCategory.WATER_AMBIENT);
            case "MISC" -> Optional.of(MobCategory.MISC);
            case "AXOLOTLS" -> Optional.of(MobCategory.AXOLOTLS);
            default -> Optional.empty();
        };
    }

    private static boolean modIdError(String modId, ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map) {
        if (map.containsKey(modId)) return false;
        CalmMornings.LOGGER.error("modId: [{}] is not in map!", modId);
        return true;
    }

    private static boolean entityIdError(String entityId, ConcurrentHashMap<String, ListInfo> map) {
        if (map.containsKey(entityId)) return false;
        CalmMornings.LOGGER.error("entityId: [{}] does not exist!", entityId);
        return true;
    }

}
