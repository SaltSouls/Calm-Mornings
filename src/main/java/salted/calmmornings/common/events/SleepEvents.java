package salted.calmmornings.common.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.capability.SleepTime;
import salted.calmmornings.common.managers.DespawnManager;
import salted.calmmornings.common.managers.TimeManager;
import salted.calmmornings.common.managers.utils.TimeUtils.Time;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepEvents {

    private static final TimeManager timeManager = new TimeManager();
    private static final DespawnManager despawnManager = new DespawnManager();

    private static void updateSleepTime(String time, Player player) {
        SleepTime.ifPresent(player, sleepPlayer -> sleepPlayer.setSleepTime(time));
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) return;

        Time dayTime = timeManager.getTimeSlice(level);
        if (dayTime == null) return; // this should never happen

        switch (dayTime) {
            case MORNING_E  -> updateSleepTime("early_morning", player);
            case MORNING    -> updateSleepTime("morning", player);
            case MORNING_L  -> updateSleepTime("late_morning", player);
            case NOON_E     -> updateSleepTime("early_afternoon", player);
            case NOON       -> updateSleepTime("afternoon", player);
            case NOON_L     -> updateSleepTime("late_afternoon", player);
            case EVENING_E  -> updateSleepTime("early_evening", player);
            case EVENING    -> updateSleepTime("evening", player);
            case EVENING_L  -> updateSleepTime("late_evening", player);
            case NIGHT_E    -> updateSleepTime("early_night", player);
            case NIGHT      -> updateSleepTime("night", player);
            case NIGHT_L    -> updateSleepTime("late_night", player);
        }
    }

    @SubscribeEvent
    public static void onSleepComplete(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        MinecraftServer server = level.getServer();
        if (server == null) return;
        if (level.isClientSide) return;

        // ensure current time is within the valid wake times
        Time levelTime = timeManager.getTimeSlice(level);
        if (!timeManager.validWakeTime(levelTime)) return;

        for (ServerPlayer players : server.getPlayerList().getPlayers()) {
            if (players == null || !timeManager.isPlayerValid(players)) continue;
            despawnManager.despawn(level, players);
        }
    }

}
