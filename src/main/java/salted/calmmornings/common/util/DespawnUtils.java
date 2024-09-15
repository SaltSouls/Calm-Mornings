package salted.calmmornings.common.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import salted.calmmornings.common.config.IConfig;

import java.util.ArrayList;
import java.util.List;

public class DespawnUtils {

    public static void despawnEntities(Level level, ServerPlayer player) {
        Difficulty difficulty = level.getDifficulty();

        if (difficulty == Difficulty.PEACEFUL) return;  // do nothing if peaceful
        double scaling = SleepUtils.scaling(difficulty);
        double h = Math.round(IConfig.getHorizontalRange() / scaling);
        double v = Math.round(IConfig.getVerticalRange() / scaling);
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
    // TODO: find a better way to do this
    // a list of entities that should not be despawned
    private static final ArrayList<EntityType<?>> blackList = new ArrayList<>(List.of(
            // bosses/dungeon enemies
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN,
            /* this should prevent raids/roaming parties from being
              affected, though there might be a better way to do this */
            EntityType.PILLAGER,
            EntityType.EVOKER,
            EntityType.ILLUSIONER,
            EntityType.RAVAGER,
            // this shouldn't happen, but better safe than sorry
            EntityType.PLAYER
    ));

    // don't despawn bedbugs if mod is loaded
    private static boolean sleeptightCompat(EntityType<?> type) {
        String mobKey = EntityType.getKey(type).toString();
        if (ModList.get().isLoaded("sleep_tight")) return !mobKey.equals("sleep_tight:bedbug");
        return true;
    }

    private static boolean shouldDespawn(@NotNull Entity entity) {
        EntityType<?> type = entity.getType();
        String mobKey = EntityType.getKey(type).toString();

        // see if the mob is in the list
        if (IConfig.getEnableList()) {
            if (IConfig.isBlacklist()) return !IConfig.getMobList().contains(mobKey);
            return IConfig.getMobList().contains(mobKey);
        }
        // see if the mob is in the category, minus blacklisted ones
        else if (!blackList.contains(type) && sleeptightCompat(type))  {
            return type.getCategory().equals(MobCategory.MONSTER);
        }

        return false;
    }

    private static void despawn(@NotNull Entity entity) {
        Level level = entity.level();

        if (shouldDespawn(entity) && !entity.hasCustomName()) {
            // get entity's position for particles
            Vec3 vec = Vec3.atBottomCenterOf(entity.blockPosition());

            // drop items with 100% drop chance(picked up/inventory items)
            if (entity instanceof Mob mob && mob.isPersistenceRequired()) {
                DamageSource source = level.damageSources().generic();
                mob.dropCustomDeathLoot(source, 0, false);
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
        if (IConfig.getEnablePlayerCheck()) return true;
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
