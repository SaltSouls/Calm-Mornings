package salted.calmmornings.common.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;
import salted.calmmornings.common.config.IConfig;

public class SleepUtils {

    public static boolean notCheater(Player player) {
        return player != null && !(player.isCreative() || player.isSpectator());
    }

    public static double scaling(Difficulty difficulty) {
        if (!IConfig.getEnableScaling()) return 1.0D;

        return switch (difficulty) {
            case NORMAL -> 2.0D;
            case HARD -> 4.0D;
            default -> 1.0D;
        };
    }

    public static boolean isPlayerValid(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) ) return false;

        ISleepTime sleepPlayer = SleepTime.get(serverPlayer);
        String sleepTime = sleepPlayer.getSleepTime();
        TimeUtils.Time playerTime = TimeUtils.getPlayerTimeSlice(serverPlayer);

        return sleepTime.equals("awake") || sleptLate(playerTime);
    }

    public static boolean validWakeTime(TimeUtils.Time time) {
        if (IConfig.getMorningCheck().equals(TimeUtils.Time.DISABLED)) return true;
        TimeUtils.Time morningTime = IConfig.getMorningCheck();

        return TimeUtils.isWithinPreviousSlices(time, morningTime);
    }

    // private methods for determining values/conditions
    private static boolean sleptLate(TimeUtils.Time time) {
        if (IConfig.getLateCheck().equals(TimeUtils.Time.DISABLED)) return false;
        TimeUtils.Time lateTime = IConfig.getLateCheck();

        return TimeUtils.isWithinFollowingSlices(time, lateTime);
    }

}
