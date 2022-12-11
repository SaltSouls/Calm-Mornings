package com.tainted.common.events;

import com.tainted.CalmMornings;
import com.tainted.common.config.ConfigHelper;
import com.tainted.common.data.SleptLateData;
import com.tainted.common.data.SleptLateProvider;
import com.tainted.common.network.PacketHandler;
import com.tainted.common.network.packet.SleptLateDataS2CPacket;
import com.tainted.common.utils.SleepUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = CalmMornings.MOD_ID)
public class SleepEvents {

    @SubscribeEvent
    public static void onPlayerSleep(@NotNull PlayerSleepInBedEvent event) {
        //SET THE PLAYERS CAPABILITY DEPENDING ON IF CERTAIN CONDITIONS ARE MET
        Player player = event.getPlayer();
        Level level = event.getEntity().getLevel();
        @NotNull LazyOptional<SleptLateData> capability = player.getCapability(SleptLateProvider.SLEPT_LATE);
        if (!level.isClientSide) {
            //SET CAPABILITY TO TRUE OR FALSE DEPENDING ON CHECK RESULTS
            if (ConfigHelper.getEnableLateCheck()) {
                capability.ifPresent(playerSleptLate -> {
                    if (!SleepUtils.isNearMorning(level)) { playerSleptLate.setNotSleptLate();
                        PacketHandler.sendToPlayer(new SleptLateDataS2CPacket(playerSleptLate.getSleptLate()), (ServerPlayer)player); }
                    else { playerSleptLate.setSleptLate();
                        PacketHandler.sendToPlayer(new SleptLateDataS2CPacket(playerSleptLate.getSleptLate()), (ServerPlayer)player); }
                });
            } else { capability.ifPresent(playerSleptLate -> { playerSleptLate.setNotSleptLate();
                PacketHandler.sendToPlayer(new SleptLateDataS2CPacket(playerSleptLate.getSleptLate()), (ServerPlayer)player); });
            }
        }
    }

    @SubscribeEvent
    public static void onSleepFinished(@NotNull SleepFinishedTimeEvent event) {
        Level level = (Level)event.getWorld();
        MinecraftServer server = level.getServer();
        if (server == null) return; //THIS SHOULDN'T HAPPEN
        Difficulty difficulty = level.getDifficulty();
        if (difficulty == Difficulty.PEACEFUL) return;  //DO NOTHING IF ON PEACEFUL
        double s = 1.0D;
        double scaling = SleepUtils.getScaling(level, s);
        double h = Math.round(ConfigHelper.getHorizontalRange() / scaling);
        double v = Math.round(ConfigHelper.getVerticalRange() / scaling);
        double ac = SleepUtils.getAntiCheese(level, 0.0D);
        int st = SleepUtils.setSleepTimer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getCapability(SleptLateProvider.SLEPT_LATE).ifPresent(playerSleptLate -> {
                //TEST IF THE PLAYER SLEPT TOO LATE AND MAKE
                //SURE THEY WERE SLEEPING FOR LONG ENOUGH
                if (!playerSleptLate.getSleptLate() && player.getSleepTimer() >= st) {
                    AABB area = SleepUtils.newAABB(player, h, v);
                    //CHECK TO SEE IF CHECKING FOR OTHER PLAYERS IS ENABLED
                    if (ConfigHelper.getEnablePlayerCheck()) {
                        Player nearby = SleepUtils.getNearbyPlayer(player, h * 1.25D);
                        //SEE IF ANOTHER PLAYER IS WITHIN RANGE OF THE PLAYER TRIGGERING
                        //THE EVENT AND NOT IN CREATIVE/SPECTATOR MODE
                        if (SleepUtils.isNotCheating(nearby) &&
                                SleepUtils.isWithinArea(player, area)) {
                            for (Player others : level.getNearbyPlayers(TargetingConditions.forNonCombat(), player, area)) {
                                //MAKE SURE THE OTHER PLAYERS AREN'T THE PLAYER TRIGGERING
                                //THE EVENT AND NOT IN CREATIVE/SPECTATOR MODE
                                if (others != player && others.getSleepTimer() < st && SleepUtils.isNotCheating(others)) {
                                    //DESPAWN ENTITIES WITHIN THE MAIN AREA THAT AREN'T AROUND OTHER PLAYERS
                                    SleepUtils.despawnSelected(player, others, area, ac);
                                    //DESPAWN ENTITIES IF ANY OF THE REST DON'T CHECK OUT
                                } else { SleepUtils.despawnSelected(player, area, ac); }
                            }
                        } else { SleepUtils.despawnSelected(player, area, ac); }
                    } else { SleepUtils.despawnSelected(player, area, ac); }
                }
            });
        }
    }

}