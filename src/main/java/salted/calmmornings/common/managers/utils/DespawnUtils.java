package salted.calmmornings.common.managers.utils;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;

import java.util.List;
import java.util.Map;

public class DespawnUtils {

    protected static double scaling(Difficulty difficulty) {
        if (!Config.ENABLE_SCALING.get()) return 1.0D;

        return switch (difficulty) {
            case NORMAL -> 2.0D;
            case HARD -> 4.0D;
            default -> 1.0D;
        };
    }

    protected boolean notCheater(Player player) {
        return player != null && !(player.isCreative() || player.isSpectator());
    }

    protected boolean isWithinArea(Entity entity, AABB area) {
        if (entity == null) return false;
        AABB entityBounds = entity.getBoundingBox();
        return entityBounds.intersects(area);
    }

    @Nullable
    protected Player getNearbyPlayer(@NotNull Player player, double distance) {
        Level level = player.level();
        Player nearbyPlayer = level.getNearestPlayer(TargetingConditions.forNonCombat(), player);
        if (!(nearbyPlayer == null || nearbyPlayer == player) && nearbyPlayer.distanceTo(player) <= distance)
            return nearbyPlayer;
        else return null;
    }

    protected String getMobGroup(String abstractPath, String explicitPath, String mobCategory) {
        Map<String, String> customCategories = Config.MOB_GROUP_MAP;
        if (customCategories.containsKey(explicitPath)) return customCategories.get(explicitPath);
        return customCategories.getOrDefault(abstractPath, mobCategory);
    }

    private static final List<String> CREATURE_CATEGORIES = List.of(
            MobCategory.CREATURE.getName(),
            MobCategory.AXOLOTLS.getName(),
            MobCategory.WATER_CREATURE.getName(),
            MobCategory.UNDERGROUND_WATER_CREATURE.getName()
    );

    private static final List<String> AMBIENT_CATEGORIES = List.of(
            MobCategory.AMBIENT.getName(),
            MobCategory.WATER_AMBIENT.getName()
    );

    private String normalizeCategory(String category) {
        if (CREATURE_CATEGORIES.contains(category)) return "creature";
        if (AMBIENT_CATEGORIES.contains(category))  return "ambient";
        return category;
    }

    protected boolean isValidGroup(EntityType<?> entity, String group) {
        if (!isValidCustomGroup(group)) group = normalizeCategory(entity.getCategory().getName());

        return switch (group) {
            case "boss"      -> Config.BOSS_CHECK.get();
            case "monster"   -> Config.MONSTER_CHECK.get();
            case "villager"  -> Config.VILLAGER_CHECK.get();
            case "creature"  -> Config.CREATURE_CHECK.get();
            case "ambient"   -> Config.AMBIENT_CHECK.get();
            case "construct" -> Config.CONSTRUCT_CHECK.get();
            case "misc"      -> Config.MISC_CHECK.get();
            default          -> false;
        };
    }

    private boolean isValidCustomGroup(String group) {
        return switch (group) {
            case "boss", "monster", "villager", "creature", "ambient", "construct", "misc", "blacklisted" -> true;
            default -> {
                CalmMornings.LOGGER.error("[{}] is not a valid group!", group);
                yield false;
            }
        };
    }

}