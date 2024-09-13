package salted.calmmornings.common.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;
import salted.calmmornings.common.config.IConfigGetter;
import salted.calmmornings.common.util.DespawnUtils;
import salted.calmmornings.common.util.SleepUtils;
import salted.calmmornings.common.util.TimeUtils;
import salted.calmmornings.common.util.TimeUtils.Time;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepEvents {

    private static void updateSleepTime(String time, Player player) {
        ISleepTime sleepPlayer = SleepTime.get(player);

        sleepPlayer.setSleepTime(time);
        sleepPlayer.syncToClient();
    }

    @SubscribeEvent
    public static void onPlayerSleep(@NotNull PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!level.isClientSide) {
            CalmMornings.LOGGER.debug("Current time slice: " + TimeUtils.getTimeSlice(level));
            Time dayTime = TimeUtils.getTimeSlice(level);
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
    }

    private static boolean lateCheck(Time time) {
        if (IConfigGetter.getLateCheck().equals(Time.DISABLED)) return false;
        Time lateTime = (Time) IConfigGetter.getLateCheck();

        return TimeUtils.isWithinFollowingSlices(time, lateTime);
    }

    @SubscribeEvent
    public static void onSleepComplete(PlayerWakeUpEvent event) {
        Level level = event.getEntity().level();
        MinecraftServer server = level.getServer();

        if (level.isClientSide) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player == null) return;
            long worldTime = TimeUtils.getTimeSlice(level).getStart();
            CalmMornings.LOGGER.debug("Current WorldTime: {}", worldTime);
            Time playerTime = TimeUtils.getPlayerTimeSlice(player);

            if (playerTime == null) return;
            Time timeChunk = TimeUtils.getPlayerTimeChunk(playerTime);
            long wakeTime = SleepUtils.getWakeTime(level);
            CalmMornings.LOGGER.debug("Wake Time: {}", wakeTime);

            switch (timeChunk) {
                case MORNING, NOON -> { return; }
                default -> {
                    if (lateCheck(playerTime)) return;
                    if (wakeTime != worldTime) return;
                    DespawnUtils.despawnEntities(level, player);
                }
            }
        }
    }

}
