package com.tainted.common.events;

import com.tainted.common.Config;
import com.tainted.common.utils.SleepUtils;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class PlayerWakeUp {

    @SubscribeEvent
    public static void onPlayerWakeup(PlayerWakeUpEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        int v = Config.VERTICAL_RANGE;
        int h = Config.HORIZONTAL_RANGE;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (!level.isClientSide && !event.wakeImmediately() && level.getDifficulty() != Difficulty.PEACEFUL) {
            System.out.println("Time: " + level.getDayTime());
            if (SleepUtils.isDay(level)) {
                if (level.getDifficulty() == Difficulty.EASY) {
                    AABB area = new AABB(x - h, y - v, z - h, x + h, y + v, z + h);
                    for (Entity entity : level.getEntities(null, area)) {
                        if (SleepUtils.shouldDespawn(entity) && !entity.hasCustomName()) {
                            entity.discard();
                        }
                    }
                } else if (level.getDifficulty() == Difficulty.NORMAL) {
                    AABB area = new AABB(x - Math.round(h/2.0), y - Math.round(v/2.0), z - Math.round(h/2.0),
                            x + Math.round(h/2.0), y + Math.round(v/2.0), z + Math.round(h/2.0));
                    for (Entity entity : level.getEntities(null, area)) {
                        if (SleepUtils.shouldDespawn(entity) && !entity.hasCustomName()
                                && entity.distanceTo(player) >= Config.ANTI_CHEESE) {
                            entity.discard();
                        }
                    }
                } else if (level.getDifficulty() == Difficulty.HARD) {
                    AABB area = new AABB(x - Math.round(h/4.0), y - Math.round(v/4.0), z - Math.round(h/4.0),
                            x + Math.round(h/4.0), y + Math.round(v/4.0), z + Math.round(h/4.0));
                    for (Entity entity : level.getEntities(null, area)) {
                        if (SleepUtils.shouldDespawn(entity) && !entity.hasCustomName()
                                && entity.distanceTo(player) >= (Config.ANTI_CHEESE * 2)) {
                            entity.discard();
                        }
                    }
                }
            }
        }
    }

}
