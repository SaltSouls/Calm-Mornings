package salted.calmmornings.common.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.registry.CMData;

import static salted.calmmornings.common.utils.TimeUtils.Time;

public class SleepUtils {

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

    public static boolean isPlayerValid(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        String sleepTime = player.getData(CMData.SLEEPTIME);
        Time playerTime = TimeUtils.getPlayerTimeSlice(serverPlayer);

        return sleepTime.equals("awake") || sleptLate(playerTime);
    }

    public static boolean validWakeTime(Time time) {
        if (Config.MORNING_CHECK.get().equals(Time.DISABLED)) return true;
        Time morningTime = Config.MORNING_CHECK.get();

        return TimeUtils.isWithinPreviousSlices(time, morningTime);
    }

    // private methods for determining values/conditions
    private static boolean sleptLate(Time time) {
        if (Config.LATE_CHECK.get().equals(Time.DISABLED)) return false;
        Time lateTime = Config.LATE_CHECK.get();

        return TimeUtils.isWithinFollowingSlices(time, lateTime);
    }

}
