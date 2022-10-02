package com.tainted.common.events;

import com.tainted.core.Config;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class PlayerWakeUp {

    public static boolean isHostile(Entity entity) {
        if (!(entity.getType() == EntityType.ENDER_DRAGON) || !(entity.getType() == EntityType.WITHER || !(entity.getType() == EntityType.ELDER_GUARDIAN) || !(entity.getType() == EntityType.GUARDIAN))) {
            return entity.getType().getCategory() == MobCategory.MONSTER;
        }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerWakeup(PlayerWakeUpEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        int r = Config.radius;
        int h = Config.height;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        AABB a0 = new AABB(x - r, y - h, z - r, x + r, y + h, z + r);
        AABB a1 = new AABB(x - Math.round(r/2.0), y - 16, z - Math.round(r/2.0), x + Math.round(r/2.0), y + 16, z + Math.round(r/2.0));
        AABB a2 = new AABB(x - Math.round(r/4.0), y - 0, z - Math.round(r/4.0), x + Math.round(r/4.0), y + 0, z + Math.round(r/4.0));

        if (!level.isClientSide && !event.wakeImmediately() && level.getDifficulty() != Difficulty.PEACEFUL) {
            if (level.getDifficulty() == Difficulty.EASY) {
                for (Entity entity : level.getEntities(null, a0)) {
                    if (isHostile(entity) && !entity.hasCustomName()) {
                        entity.discard();
                    }
                }
            } else if (level.getDifficulty() == Difficulty.NORMAL) {
                for (Entity entity : level.getEntities(null, a1)) {
                    if (isHostile(entity) && !entity.hasCustomName() && entity.distanceTo(player) >= Config.anticheese) {
                        entity.discard();
                    }
                }
            } else if (level.getDifficulty() == Difficulty.HARD) {
                for (Entity entity : level.getEntities(null, a2)) {
                    if (isHostile(entity) && !entity.hasCustomName() && entity.distanceTo(player) >= (Config.anticheese * 2)) {
                        entity.discard();
                    }
                }
            }
        }
    }

}
