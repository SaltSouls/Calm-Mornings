package com.tainted.common.events;

import com.tainted.CalmMornings;
import com.tainted.common.config.ConfigHelper;
import com.tainted.common.data.SleptLateProvider;
import com.tainted.common.network.PacketHandler;
import com.tainted.common.network.packet.SleptLateC2SPacket;
import com.tainted.common.network.packet.SleptLateDataS2CPacket;
import com.tainted.common.utils.SleepUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = CalmMornings.MOD_ID)
public class SleepEvents {

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        //UPDATE PLAYER CAPABILITY ON ATTEMPTING TO SLEEP
        PacketHandler.sendToServer(new SleptLateC2SPacket());
    }

    @SubscribeEvent
    public static void onSleepFinished(@NotNull SleepFinishedTimeEvent event) {
        Level level = (Level) event.getLevel();
        MinecraftServer server = level.getServer();
        if (server == null) return; //THIS SHOULDN'T HAPPEN
        Difficulty difficulty = level.getDifficulty();
        if (difficulty == Difficulty.PEACEFUL) return; //DO NOTHING IF ON PEACEFUL
        double h = ConfigHelper.getHorizontalRange();
        double v = ConfigHelper.getVerticalRange();
        double s = 1.0D;
        double ac = SleepUtils.getAntiCheese(level, 0.0D);
        int t = ConfigHelper.getSleepTimer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getCapability(SleptLateProvider.SLEPT_LATE).ifPresent(playerSleptLate -> {
                PacketHandler.sendToPlayer(new SleptLateDataS2CPacket(playerSleptLate.getSleptLate()), player);
                //TEST IF THE PLAYER SLEPT TOO LATE AND MAKE SURE THEY WERE SLEEPING FOR LONG ENOUGH
                if (!playerSleptLate.getSleptLate()) {
                    if (player.getSleepTimer() >= t) {
                        AABB area = SleepUtils.newAABB(player, h, v);
                        //CHECK TO SEE IF CHECKING FOR OTHER PLAYERS IS ENABLED
                        if (ConfigHelper.getEnablePlayerCheck()) {
                            Player nearby = level.getNearestPlayer(player, h / SleepUtils.getScaling(level, s));
                            //SEE IF ANOTHER PLAYER IS WITHIN RANGE OF THE PLAYER THE EVENT IS TAKING PLACE AROUND
                            if (nearby != null && nearby != player) {
                                AABB playerBounds = nearby.getBoundingBox();
                                if (area.intersects(playerBounds)) {    //TEST IF ANOTHER PLAYER IS WITHIN THE AREA
                                    for (Player others : level.getNearbyPlayers(TargetingConditions.forNonCombat(), player, area)) {
                                        //MAKE SURE THE OTHER PLAYERS AREN'T THE PLAYER TRIGGERING THE EVENT
                                        if (others != player && others.getSleepTimer() >= t) {
                                            //DESPAWN ENTITIES WITHIN THE MAIN AREA THAT AREN'T AROUND OTHER PLAYERS
                                            AABB area1 = SleepUtils.newAABB(others, h / 2, v / 2);
                                            AABB exclusion = area.intersect(area1);
                                            SleepUtils.despawnSelected(level, player, area, exclusion, ac);
                                        }
                                    }
                                    //DESPAWN ENTITIES IF ANY OF THE REST DON'T CHECK OUT
                                } else { SleepUtils.despawnSelected(level, player, area, ac); }
                            } else { SleepUtils.despawnSelected(level, player, area, ac); }
                        } else { SleepUtils.despawnSelected(level, player, area, ac); }
                    }
                }
            });
        }
    }

}
