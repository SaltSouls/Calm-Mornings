package salted.calmmornings.common.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.registry.CMData;

public class SleepUtils {

    private static final int dayLength = Level.TICKS_PER_DAY;

    public static boolean notCheater(Player player) {
        return player != null && !(player.isCreative() || player.isSpectator());
    }

    public static double scaling(Difficulty difficulty) {
        if (Config.ENABLE_SCALING.get()) return 1.0D;

        return switch (difficulty) {
            case NORMAL -> 2.0D;
            case HARD -> 4.0D;
            default -> 1.0D;
        };
    }

    private static boolean sleptLate(TimeUtils.Time time) {
        if (Config.LATE_CHECK.get().equals(TimeUtils.Time.DISABLED)) return false;
        TimeUtils.Time lateTime = Config.LATE_CHECK.get();

        return TimeUtils.isWithinFollowingSlices(time, lateTime);
    }

    public static boolean isPlayerValid(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) ) return false;

        String sleepTime = player.getData(CMData.SLEEPTIME);
        TimeUtils.Time playerTime = TimeUtils.getPlayerTimeSlice(serverPlayer);

        return sleepTime.equals("awake") || sleptLate(playerTime);
    }

    // simple function for now until future methods are implemented
    public static long getWakeTime(Level level) {
        long dayRemainder = level.getDayTime() % dayLength;
        long wakeTime = TimeUtils.Time.MORNING_E.getStart();

        return Math.abs(wakeTime - ((dayRemainder + dayLength) % dayLength));
    }

}
