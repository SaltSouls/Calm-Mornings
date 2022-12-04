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
public class PlayerSleepEvents {
//    @SubscribeEvent
//    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
//        Player player = event.getEntity();
//        Level level = player.getLevel();
//        long sleepTime = level.getDayTime();
//        if (!level.isClientSide && !SleepUtils.isDay(level)) {
//            setIsLateTrue(player);
//        }
//        setIsLateFalse(player);
//    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {

        Player player = event.getEntity();
        Level level = player.level;
        Difficulty difficulty = level.getDifficulty();

        int v = Config.VERTICAL_RANGE;
        int h = Config.HORIZONTAL_RANGE;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        double sf = 1;
        double acf = 0;

        if (Config.ENABLE_SCALING) {
            switch (difficulty) {
                case NORMAL -> {
                    sf = 2;
                    acf = Config.ANTI_CHEESE;
                    break;
                }
                case HARD -> {
                    sf = 4;
                    acf = Config.ANTI_CHEESE * 2;
                }
            }
        }

        if (!level.isClientSide && !event.wakeImmediately() && difficulty != Difficulty.PEACEFUL) {
            System.out.println("Too late on wake: " + SleepUtils.isNearMorning(level));
            if (SleepUtils.isDay(level)) {
                AABB area = new AABB(x - Math.round(h/sf), y - Math.round(v/sf), z - Math.round(h/sf), x + Math.round(h/sf), y + Math.round(v/sf), z + Math.round(h/sf));
                for (Entity entity : level.getEntities(null, area)) {
                    if (SleepUtils.shouldDespawn(entity) && !entity.hasCustomName()
                            && entity.distanceTo(player) >= acf) {
                        entity.discard();
                    }
                }
            }
        }
    }

}
