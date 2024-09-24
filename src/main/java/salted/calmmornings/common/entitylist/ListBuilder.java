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

public class ListBuilder {
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> getEntityMap() {
        return entityMap;
    }
    public static ImmutableMap<String, ListInfo> getEntityIdMap(String modId) {
        return ImmutableMap.copyOf(entityMap.get(modId));
    }
    public static HashSet<MobCategory> getFilterList() {
        return filterList;
    }

    // entity despawn value methods
    public static void addEntity(@NotNull String entity, EntityType<?> type, boolean isBlackList) {
        // get mod/entity id if they exist
        Optional<Pair<String, String>> optional = entityKey(entity);
        if (optional.isEmpty()) return;
        Pair<String, String> key = optional.get();
        String modId = key.getLeft();
        String entityId = key.getRight();

        // get outer/inner hashmaps
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = entityMap;
        ConcurrentHashMap<String, ListInfo> innerMap = new ConcurrentHashMap<>();

        // check if list is enabled else use default values
        if (Config.ENABLE_LIST.get()) innerMap.put(entityId, new ListInfo(type.getCategory(), isBlackList));
        else innerMap.put(entityId, new ListInfo(type.getCategory(), true));

        // get the mod map if it exists, else create map
        if (map.containsKey(modId)) map.get(modId).putAll(innerMap);
        else map.put(modId, innerMap);
    }

    public static void updateEntity(Pair<String, String> key, boolean value) {
        // check if mod/entity ids are valid
        String modId = key.getLeft();
        String entityId = key.getRight();
        if (isNotValidEntity(modId, entityId)) return;

        // get inner hashmap
        ConcurrentHashMap<String, ListInfo> innerMap = entityMap.get(modId);

        CalmMornings.LOGGER.debug("Configured: [{}:{}] to {}", modId, entityId, value);
        innerMap.get(entityId).setDespawnable(value);
    }

    public static void flipAllValues() {
        entityMap.forEach((modId, innerMap) ->
            innerMap.forEach((entityId, listInfo) ->
                listInfo.setDespawnable(!listInfo.getDespawnable())
            )
        );
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

    // entity category methods
    public static void updateEntityCategory(Triple<String, String, String> key) {
        // check if mod/entity ids are valid
        String modId = key.getLeft();
        String entityId = key.getMiddle();
        if (isNotValidEntity(modId, entityId)) return;

        // check if MobCategory actually exists
        String category = key.getRight();
        Optional<MobCategory> categoryOpt = isValidCategory(category);
        if (categoryOpt.isEmpty()) return;
        MobCategory mobCategory = categoryOpt.get();

        // get inner hashmap
        ConcurrentHashMap<String, ListInfo> innerMap = entityMap.get(modId);

        CalmMornings.LOGGER.debug("Configured: [{}:{}] category to {}", modId, entityId, mobCategory.getName());
        if (innerMap.containsKey(entityId)) innerMap.get(entityId).setCategory(mobCategory);
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

    public static Optional<Triple<String, String, String>> categoryKey(String entity) {
        String[] key = entity.split(":");

        // get the modId(key[0]), entityId(key[1]) abd mobCategory(key[2]) from entity
        if (key.length < 3) {
            CalmMornings.LOGGER.error("[{}:{}] is not a valid category entry!", key[0], key[1]);
            return Optional.empty();
        }
        return Optional.of(Triple.of(key[0], key[1], key[2]));
    }

    // private methods for determining values/conditions
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> entityMap = new ConcurrentHashMap<>();
    private static final HashSet<MobCategory> filterList = new HashSet<>();

    private static boolean isNotValidEntity(String modId, String entityId) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = entityMap;

        // check if modId actually exists
        if (!map.containsKey(modId)) {
            CalmMornings.LOGGER.error("modId: [{}] does not exist!", modId);
            return true;
        }

        // check if entityId actually exists
        ConcurrentHashMap<String, ListInfo> innerMap = map.get(modId);
        if (!innerMap.containsKey(entityId)) {
            CalmMornings.LOGGER.error("entityId: [{}] does not exist!", entityId);
            return true;
        }

        return false;
    }

    private static Optional<MobCategory> isValidCategory(String category) {
        return switch (category) {
            case "monster" -> Optional.of(MobCategory.MONSTER);
            case "creature" -> Optional.of(MobCategory.CREATURE);
            case "axolotls" -> Optional.of(MobCategory.AXOLOTLS);
            case "water_creature" -> Optional.of(MobCategory.WATER_CREATURE);
            case "underground_water_creature" -> Optional.of(MobCategory.UNDERGROUND_WATER_CREATURE);
            case "ambient" -> Optional.of(MobCategory.AMBIENT);
            case "water_ambient" -> Optional.of(MobCategory.WATER_AMBIENT);
            case "misc" -> Optional.of(MobCategory.MISC);
            default -> {
                CalmMornings.LOGGER.error("mobCategory: [{}] does not exist!", category);
                yield Optional.empty();
            }
        };
    }

}
