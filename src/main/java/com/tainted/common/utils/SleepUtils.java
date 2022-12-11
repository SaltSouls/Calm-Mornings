package com.tainted.common.utils;

import com.tainted.common.config.ConfigHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SleepUtils {

    public static boolean isNearMorning(@NotNull Level level) {
        long getTime = level.getDayTime();
        return getTime % 22500 < 12500;
    }

    public static double getAntiCheese(@NotNull Level level, double anticheese) {
        //GET THE ANTI-CHEESE SCALING BASED ON DIFFICULTY OF THE CURRENT LEVEL
        Difficulty difficulty = level.getDifficulty();
        if (ConfigHelper.getEnableScaling() && ConfigHelper.getAntiCheese() > 0.0D
                && difficulty == Difficulty.HARD) {
            anticheese = 2.0D;
        }
        return anticheese;
    }

    public static double getScaling(@NotNull Level level, double scaling) {
        //GET SCALING BASED ON THE DIFFICULTY OF THE CURRENT LEVEL
        Difficulty difficulty = level.getDifficulty();
        if (ConfigHelper.getEnableScaling()) {
            if (difficulty == Difficulty.NORMAL) {
                scaling = 2.0D;
            } else if (difficulty == Difficulty.HARD) {
                scaling = 4.0D;
            }
        }
        return scaling;
    }

    public static int setSleepTimer() {
        if (ModList.get().isLoaded("hourglass")) { return 10; }
        return ConfigHelper.getSleepTimer();
    }

    public static boolean isNotCheating(Player player) {
        return player != null && !(player.isCreative() || player.isSpectator());
    }

    @NotNull
    public static AABB newAABB(@NotNull Entity entity, double horizontal, double vertical) {
        //CREATE NEW AXIS ALIGNED BOUNDING BOX FOR AN ENTITY PASSED THROUGH
        Level level = entity.getLevel();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        return new AABB(x - horizontal, y - vertical, z - horizontal, x + horizontal, y + vertical, z + horizontal);
    }

    @Nullable
    public static Player getNearbyPlayer(@NotNull Player player, double distance) {
        Level level = player.getLevel();
        Player nearby = level.getNearestPlayer(TargetingConditions.forNonCombat(), player);
        if (!(nearby == null || nearby == player) && nearby.distanceTo(player) <= distance) { return nearby; }
        else { return null; }
    }

    public static boolean isWithinArea(Entity entity, AABB area) {
        if (entity != null) {
            AABB entityBounds = entity.getBoundingBox();
            return entityBounds.intersects(area);
        }
        return false;
    }

    public static boolean shouldDespawn(@NotNull Entity entity) {
        //CHECKS TO SEE IF THE MOB TO DESPAWN IF VALID
        EntityType<?> type = entity.getType();
        if (ConfigHelper.getEnableList()) {
            String mobKey = EntityType.getKey(type).toString();
            //GET VALID MOBS BY SEEING IF THE LIST CONTAINS THE MOB KEY
            return ConfigHelper.getMobList().contains(mobKey);
        //GET VALID MOBS BY SEEING IF THEY ARE OF THE MONSTER CATEGORY
        } else if (type != EntityType.ENDER_DRAGON || type != EntityType.WITHER
                || type != EntityType.ELDER_GUARDIAN || type != EntityType.GUARDIAN) {
            return type.getCategory() == MobCategory.MONSTER;
        }
        return false;
    }

    public static void despawn(@NotNull Entity entity) {
        //DESPAWN THE SELECTED ENTITY
        Level level = entity.getLevel();
        //MAKE SURE THE ENTITY IS VALID BUT NOT A PLAYER/HAS A CUSTOM NAME
        if (shouldDespawn(entity) && !(entity instanceof Player) && !entity.hasCustomName()) {
            //GET ENTITY POS FOR PARTICLE SPAWN
            double x = entity.getX();
            double y = entity.getY() + 1.0D;
            double z = entity.getZ();
            //REMOVE ENTITY AND SPAWN PARTICLES
            entity.discard();
            ((ServerLevel)level).sendParticles(ParticleTypes.POOF, x, y, z, 15, 0.05D, 0.50D, 0.05D, 0.001D);
        }
    }

    public static void despawnSelected(@NotNull Player player, Player player2 , AABB area, double anticheese) {
        //GET SELECTED ENTITY TO DESPAWN AND CHECK TO MAKE SURE THEY AREN'T AROUND ANOTHER PLAYER
        Level level = player.getLevel();
        AABB area1 = newAABB(player2, 8.0D * (getScaling(level, 1.0) / 2.0D), 6.0D);
        AABB exclusion = area1.intersect(area);
        for (Entity entity : level.getEntities(null, area)) {
            //SEE IF ENTITY IS AROUND ANOTHER PLAYER
            if (entity.distanceTo(player) >= anticheese) {
                if (!isWithinArea(entity, exclusion)) { despawn(entity); }
            }
        }
    }

    public static void despawnSelected(@NotNull Player player, AABB area, double anticheese) {
        //GET SELECTED ENTITY TO DESPAWN
        Level level = player.getLevel();
        for (Entity entity : level.getEntities(null, area)) {
            if (entity.distanceTo(player) >= anticheese) { despawn(entity); }
        }
    }

}
