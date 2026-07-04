package salted.calmmornings.common.managers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.managers.utils.DespawnUtils;
import salted.calmmornings.common.tags.CMTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DespawnManager extends DespawnUtils {

    @NotNull
    public AABB newAABB(@NotNull Entity entity, double horizontal, double vertical) {
        Vec3 vec3 = Vec3.atBottomCenterOf(entity.getOnPos());
        return new AABB(vec3.x() - horizontal, vec3.y() - vertical, vec3.z() - horizontal, vec3.x() + horizontal, vec3.y() + vertical, vec3.z() + horizontal);
    }

    public void despawn(Level level, ServerPlayer player) {
        Difficulty difficulty = level.getDifficulty();
        if (difficulty == Difficulty.PEACEFUL) return;

        double scaling = scaling(difficulty);
        double h = Math.round(Config.HORIZONTAL_RANGE.get() / scaling);
        double v = Math.round(Config.VERTICAL_RANGE.get() / scaling);
        AABB area = newAABB(player, h, v);

        // player-check off, or no valid player near the sleeper → clear everything
        if (!hasNearbyPlayer(player, h, area)) {
            despawnSelected(player, area);
            return;
        }

        // collect one protected zone per valid nearby player
        List<AABB> exclusions = new ArrayList<>();
        for (Player other : level.getNearbyPlayers(TargetingConditions.forNonCombat(), player, area)) {
            if (!isOtherPlayerValid(player, other)) continue;
            exclusions.add(exclusionFor(other, area, difficulty));
        }

        // despawn anything not shielded by any awake player zones
        if (!exclusions.isEmpty()) despawnExcluding(player, area, exclusions);
    }

    private boolean shouldDespawn(@NotNull Entity entity) {
        EntityType<?> type = entity.getType();
        ResourceLocation entityKey = EntityType.getKey(type);
        String abstractPath = entityKey.getNamespace() + ":*";
        String explicitPath = entityKey.toString();
        String group = getMobGroup(abstractPath, explicitPath, type.getCategory().getName());

        if (isBlacklisted(type, group)) return false;

        // gather entities by either group/list depending on selected mode
        if (Config.ENABLE_LIST.get()) {
            return Config.MOB_SET.contains(abstractPath) || Config.MOB_SET.contains(explicitPath);
        }
        return isValidGroup(type, group);
    }

    private boolean isBlacklisted(EntityType<?> type, String group) {
        // checks for blacklisted mobs using either config or entity tag
        if (group.equals("blacklisted")) return true;
        return !Config.ENABLE_LIST.get() && type.is(CMTags.DEFAULT_BLACKLIST);
    }

    private void despawnEntity(@NotNull Entity entity) {
        Level level = entity.level();

        // never despawn blacklisted/named mobs
        if (!shouldDespawn(entity) || entity.hasCustomName()) return;
        // get entities position for particles
        Vec3 vec = Vec3.atBottomCenterOf(entity.blockPosition());
        if (entity instanceof Mob mob) { dropCustomEquipment(mob); }
        entity.discard();

        // spawn poof particles at previous entity location
        if (level.isClientSide) return;
        ServerLevel serverLevel = Objects.requireNonNull(level.getServer()).getLevel(level.dimension());
        assert serverLevel != null;
        serverLevel.sendParticles(ParticleTypes.POOF, vec.x(), vec.y() + 1.0D, vec.z(), 15, 0.05D, 0.50D, 0.05D, 0.001D);
    }

    private void dropCustomEquipment(Mob mob) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = mob.getItemBySlot(slot);
            if (stack.isEmpty() || mob.getEquipmentDropChance(slot) < 1.0F) continue;

            mob.spawnAtLocation(stack);
            mob.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    private boolean isOtherPlayerValid(Player player, Player other) {
        if (!(player instanceof ServerPlayer && other instanceof ServerPlayer)) return false;
        return !other.equals(player) && notCheater(other);
    }

    private void despawnSelected(@NotNull Player player, AABB area) {
        Level level = player.level();
        for (Entity entity : level.getEntities(null, area)) despawnEntity(entity);
    }

    private boolean hasNearbyPlayer(Player player, double h, AABB area) {
        if (!Config.PLAYER_CHECK.get()) return false;
        Player nearby = getNearbyPlayer(player, h * 1.25D);
        return !player.equals(nearby) && notCheater(nearby) && isWithinArea(player, area);
    }

    private AABB exclusionFor(Player other, AABB area, Difficulty difficulty) {
        AABB box = newAABB(other, 8.0D * (scaling(difficulty) / 2.0D), 6.0D);
        return box.intersect(area);
    }

    private void despawnExcluding(Player player, AABB area, List<AABB> exclusions) {
        Level level = player.level();
        for (Entity entity : level.getEntities(null, area)) {
            if (exclusions.stream().noneMatch(zone -> isWithinArea(entity, zone))) {
                despawnEntity(entity);
            }
        }
    }

}