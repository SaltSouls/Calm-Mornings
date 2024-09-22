package salted.calmmornings.common.utils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.entitylist.ListBuilder;
import salted.calmmornings.common.entitylist.ListInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DespawnUtils {

    public static void despawnEntities(Level level, ServerPlayer player) {
        Difficulty difficulty = level.getDifficulty();

        if (difficulty == Difficulty.PEACEFUL) return;  // do nothing if peaceful
        double scaling = SleepUtils.scaling(difficulty);
        double h = Math.round(Config.HORIZONTAL_RANGE.get() / scaling);
        double v = Math.round(Config.VERTICAL_RANGE.get() / scaling);
        AABB area = newAABB(player, h, v);

        // check to see if player check is enabled in config
        if (!isPlayerCheckEnabled(player, area)) return;
        // sees if another player is within the range of the main player
        if (!isPlayerNearby(player, h, area)) return;

        for (Player others : level.getNearbyPlayers(TargetingConditions.forNonCombat(), player, area)) {
            // makes sure the other player isn't the main player, sleeping, or cheating
            if (!isOtherPlayerValid(player, others, area)) return;
            despawnSelected(player, others, area);
        }
    }

    // private methods for despawning entities
    private static boolean shouldDespawn(@NotNull Entity entity) {
        EntityType<?> type = entity.getType();
        String entityKey = EntityType.getKey(type).toString();
        Optional<Tuple<String, String>> optional = ListBuilder.entityKey(entityKey);
        if (optional.isEmpty()) return false;

        Tuple<String, String> key = optional.get();
        String modId = key.getA();
        String entityId = key.getB();

        ConcurrentHashMap<String, ConcurrentHashMap<String, ListInfo>> map = ListBuilder.getEntityMap();
        ListInfo listInfo = map.get(modId).get(entityId);

        if (Config.ENABLE_LIST.get()) return listInfo.getDespawnable() && ListBuilder.getFilterList().contains(listInfo.getCategory());
        return (listInfo.getCategory() == MobCategory.MONSTER && listInfo.getDespawnable());
    }

    private static void despawn(@NotNull Entity entity) {
        Level level = entity.level();

        if (shouldDespawn(entity) && !entity.hasCustomName()) {
            // get entity's position for particles
            Vec3 vec = Vec3.atBottomCenterOf(entity.blockPosition());

            // drop items with 100% drop chance(picked up/inventory items)
            if (entity instanceof Mob mob && mob.isPersistenceRequired()) {
                mob.dropPreservedEquipment();
                mob.discard();
            } else if (entity instanceof LivingEntity livingEntity) {
                if(livingEntity instanceof Player) return; // this should never happen
                livingEntity.dropEquipment();
                livingEntity.discard();
            } else entity.discard();

            // spawn poof particles
            if (!(level instanceof ServerLevel serverLevel)) return;
            serverLevel.sendParticles(ParticleTypes.POOF, vec.x(), vec.y() + 1.0D, vec.z(), 15, 0.05D, 0.50D, 0.05D, 0.001D);
        }
    }

    @NotNull
    public static AABB newAABB(@NotNull Entity entity, double horizontal, double vertical) {
        Vec3 vec3 = Vec3.atBottomCenterOf(entity.getOnPos());
        return new AABB(vec3.x() - horizontal, vec3.y() - vertical, vec3.z() - horizontal, vec3.x() + horizontal, vec3.y() + vertical, vec3.z() + horizontal);
    }

    @Nullable
    private static Player getNearbyPlayer(@NotNull Player player, double distance) {
        Level level = player.level();
        Player nearbyPlayer = level.getNearestPlayer(TargetingConditions.forNonCombat(), player);
        if (!(nearbyPlayer == null || nearbyPlayer == player) && nearbyPlayer.distanceTo(player) <= distance)
            return nearbyPlayer;
        else return null;
    }

    private static boolean isWithinArea(Entity entity, AABB area) {
        if (entity == null) return false;
        AABB entityBounds = entity.getBoundingBox();
        return entityBounds.intersects(area);
    }

    private static void despawnSelected(@NotNull Player player, Player player2, AABB area) {
        Level level = player.level();
        Difficulty difficulty = level.getDifficulty();

        AABB area1 = newAABB(player2, 8.0D * (SleepUtils.scaling(difficulty) / 2.0D), 6.0D);
        AABB exclusion = area1.intersect(area);
        for (Entity entity : level.getEntities(null, area)) {
            if (!isWithinArea(entity, exclusion)) despawn(entity);
        }
    }

    private static void despawnSelected(@NotNull Player player, AABB area) {
        Level level = player.level();
        for (Entity entity : level.getEntities(null, area)) despawn(entity);
    }

    private static boolean isPlayerCheckEnabled(Player player, AABB area) {
        if (Config.PLAYER_CHECK.get()) return true;
        despawnSelected(player, area);
        return false;
    }

    private static boolean isPlayerNearby(Player player, double h, AABB area) {
        Player nearby = getNearbyPlayer(player, h * 1.25D);
        if (!player.equals(nearby) && SleepUtils.notCheater(nearby) && isWithinArea(player, area)) return true;
        despawnSelected(player, area);
        return false;
    }

    private static boolean isOtherPlayerValid(Player player, Player player2, AABB area) {
        if (!player2.equals(player) && !SleepUtils.isPlayerValid(player) && SleepUtils.notCheater(player2)) return true;
        despawnSelected(player, area);
        return false;
    }

}
