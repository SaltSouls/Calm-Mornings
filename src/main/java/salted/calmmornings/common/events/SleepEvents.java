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
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;
import salted.calmmornings.common.managers.DespawnManager;
import salted.calmmornings.common.managers.TimeManager;
import salted.calmmornings.common.managers.utils.TimeUtils.Time;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepEvents {

    private static void updateSleepTime(String time, Player player) {
        ISleepTime sleepPlayer = SleepTime.get(player);
        sleepPlayer.setSleepTime(time);
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide && !(player instanceof ServerPlayer)) return;
        TimeManager timeManager = new TimeManager();

        Time dayTime = timeManager.getTimeSlice(level);
        if (dayTime == null) return; // this should never happen

        switch (dayTime) {
            case MORNING_E -> updateSleepTime("early_morning", player);
            case MORNING -> updateSleepTime("morning", player);
            case MORNING_L -> updateSleepTime("late_morning", player);
            case NOON_E -> updateSleepTime("early_afternoon", player);
            case NOON -> updateSleepTime("afternoon", player);
            case NOON_L -> updateSleepTime("late_afternoon", player);
            case EVENING_E -> updateSleepTime("early_evening", player);
            case EVENING -> updateSleepTime("evening", player);
            case EVENING_L -> updateSleepTime("late_evening", player);
            case NIGHT_E -> updateSleepTime("early_night", player);
            case NIGHT -> updateSleepTime("night", player);
            case NIGHT_L -> updateSleepTime("late_night", player);
        }
    }

    @SubscribeEvent
    public static void onSleepComplete(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        MinecraftServer server = level.getServer();

        if (level.isClientSide && !(player instanceof ServerPlayer)) return;
        for (ServerPlayer players : server.getPlayerList().getPlayers()) {
            // early return if player isn't sleeping/slept late
            TimeManager timeManager = new TimeManager();
            if (!timeManager.isPlayerValid(players)) return;

            Time levelTime = timeManager.getTimeSlice(level);
            Time playerTime = timeManager.getPlayerTimeSlice(players);
            Time timeChunk = timeManager.getPlayerTimeChunk(playerTime);

            switch (timeChunk) {
                case EVENING, NIGHT -> {
                    if (!timeManager.validWakeTime(levelTime)) return;

                    DespawnManager despawnManager = new DespawnManager();
                    despawnManager.despawn(level, players, timeManager);
                }
            }
        }
    }

}
