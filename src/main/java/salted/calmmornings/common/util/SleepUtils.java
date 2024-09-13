package salted.calmmornings.common.util;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import salted.calmmornings.common.config.IConfigGetter;

public class SleepUtils {

    private static final int dayLength = Level.TICKS_PER_DAY;

    public static boolean notCheater(Player player) {
        return player != null && !(player.isCreative() || player.isSpectator());
    }

    public static double scaling(Difficulty difficulty) {
        return switch (difficulty) {
            case NORMAL -> 2.0D;
            case HARD -> 4.0D;
            default -> 1.0D;
        };
    }

    public static int timeReq() {
        return IConfigGetter.getSleepTimer();
    }

    // simple function for now until future methods are implemented
    public static long getWakeTime(Level level) {
        long dayRemainder = level.getDayTime() % dayLength;
        long wakeTime = TimeUtils.Time.MORNING_E.getStart();

        return Math.abs(wakeTime - ((dayRemainder + dayLength) % dayLength));
    }

}
