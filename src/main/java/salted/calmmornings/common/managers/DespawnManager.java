package salted.calmmornings.common.managers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.common.config.IConfig;
import salted.calmmornings.common.managers.utils.DespawnUtils;
import salted.calmmornings.common.tags.CMTags;

public class DespawnManager extends DespawnUtils {

    @NotNull
    public AABB newAABB(@NotNull Entity entity, double horizontal, double vertical) {
        Vec3 vec3 = Vec3.atBottomCenterOf(entity.getOnPos());
        return new AABB(vec3.x() - horizontal, vec3.y() - vertical, vec3.z() - horizontal, vec3.x() + horizontal, vec3.y() + vertical, vec3.z() + horizontal);
    }

    public void despawn(Level level, ServerPlayer player, TimeManager timeManager) {
        Difficulty difficulty = level.getDifficulty();

        if (difficulty == Difficulty.PEACEFUL) return;  // do nothing if peaceful
        double scaling = scaling(difficulty);
        double h = Math.round(IConfig.getHorizontalRange() / scaling);
        double v = Math.round(IConfig.getVerticalRange() / scaling);
        AABB area = newAABB(player, h, v);

        // see if we should check for players and if they're nearby
        if (!playerCheck(player, h, area)) return;

        for (Player others : level.getNearbyPlayers(TargetingConditions.forNonCombat(), player, area)) {
            // makes sure the other player isn't the main player, sleeping, or cheating
            if (!isOtherPlayerValid(player, others, area, timeManager)) return;
            despawnSelected(player, others, area);
        }
    }

    private boolean shouldDespawn(@NotNull Entity entity) {
        ResourceLocation entityKey = EntityType.getKey(entity.getType());
        String mobCategory = entity.getType().getCategory().getName();
        String modId = entityKey.getNamespace();
        String abstractPath = modId + ":*";
        String explicitPath = entityKey.toString();

        // for check/get custom entity categories
        String group = getMobGroup(abstractPath, explicitPath, mobCategory);
        if (!IConfig.getEnableList()) return shouldDespawnBuiltin(entity.getType(), group);

        // check if the mob is a valid entity and attempt to despawn
        boolean validMob = IConfig.getMobSet().contains(abstractPath) || IConfig.getMobSet().contains(explicitPath);
        return validMob && isValidGroup(entity.getType(), group);
    }

    private boolean shouldDespawnBuiltin(EntityType<?> entity, String group) {
        return group.equals(MobCategory.MONSTER.getName()) && !entity.is(CMTags.DEFAULT_BLACKLIST);
    }

    private void despawnEntity(@NotNull Entity entity) {
        Level level = entity.getLevel();

        // ignore entities with custom names
        if (shouldDespawn(entity) && !entity.hasCustomName()) {
            // get entity's position for particles
            Vec3 vec = Vec3.atBottomCenterOf(entity.blockPosition());

            // drop items with 100% drop chance(picked up/inventory items)
            if (entity instanceof Mob mob && mob.isPersistenceRequired()) {
                DamageSource source = level.damageSources().genericKill();
                mob.dropCustomDeathLoot(source, 0, false);
                mob.discard();
            } else if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity instanceof Player) return; // this should never happen
                livingEntity.dropEquipment();
                livingEntity.discard();
            } else entity.discard();

            // spawn poof particles
            if (!(level instanceof ServerLevel serverLevel)) return;
            serverLevel.sendParticles(ParticleTypes.POOF, vec.x(), vec.y() + 1.0D, vec.z(), 15, 0.05D, 0.50D, 0.05D, 0.001D);
        }
    }

    private void despawnSelected(@NotNull Player player, Player player2, AABB area) {
        Level level = player.level();
        Difficulty difficulty = level.getDifficulty();

        AABB area1 = newAABB(player2, 8.0D * (scaling(difficulty) / 2.0D), 6.0D);
        AABB exclusion = area1.intersect(area);
        for (Entity entity : level.getEntities(null, area)) {
            if (!isWithinArea(entity, exclusion)) despawnEntity(entity);
        }
    }

    private void despawnSelected(@NotNull Player player, AABB area) {
        Level level = player.level();
        for (Entity entity : level.getEntities(null, area)) despawnEntity(entity);
    }

    private boolean playerCheck(Player player, double h, AABB area) {
        if (IConfig.getPlayerCheck()) {
            Player nearby = getNearbyPlayer(player, h * 1.25D);
            if (!player.equals(nearby) && notCheater(nearby) && isWithinArea(player, area)) return true;
        }
        despawnSelected(player, area);
        return false;
    }

    private boolean isOtherPlayerValid(Player player, Player player2, AABB area, TimeManager timeManager) {
        if (!(player instanceof ServerPlayer && player2 instanceof ServerPlayer)) return false;
        if (!player2.equals(player) && timeManager.isPlayerValid(player) && notCheater(player2)) return true;
        despawnSelected(player, area);
        return false;
    }

}
