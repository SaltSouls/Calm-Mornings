package salted.calmmornings.common.managers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.managers.utils.TimeUtils;
import salted.calmmornings.common.registry.CMData;

public class TimeManager extends TimeUtils {

    public Time getTimeSlice(Level level) {
        Time timeChunk = getTimeChunk(level);
        if (timeChunk == null) return null; // this should never happen

        return switch (timeChunk) {
            case MORNING -> determineTimeSlice(level, Time.MORNING);
            case NOON -> determineTimeSlice(level, Time.NOON);
            case EVENING -> determineTimeSlice(level, Time.EVENING);
            case NIGHT -> determineTimeSlice(level, Time.NIGHT);
            default -> timeError("time");
        };
    }

    public Time getPlayerTimeSlice(Player player) {
        String sleepTime = player.getData(CMData.SLEEPTIME);

        return switch (sleepTime) {
            case "early_morning" -> Time.MORNING_E;
            case "morning" -> Time.MORNING;
            case "late_morning" -> Time.MORNING_L;
            case "early_afternoon" -> Time.NOON_E;
            case "afternoon" -> Time.NOON;
            case "late_afternoon" -> Time.NOON_L;
            case "early_evening" -> Time.EVENING_E;
            case "evening" -> Time.EVENING;
            case "late_evening" -> Time.EVENING_L;
            case "early_night" -> Time.NIGHT_E;
            case "night" -> Time.NIGHT;
            case "late_night" -> Time.NIGHT_L;
            default -> timeError("sleep time");
        };
    }

    public Time getPlayerTimeChunk(Time time) {
        if (isBetweenTimeSlice(time, Time.MORNING)) return Time.MORNING;
        else if (isBetweenTimeSlice(time, Time.NOON)) return Time.NOON;
        else if (isBetweenTimeSlice(time, Time.EVENING)) return Time.EVENING;
        else if (isBetweenTimeSlice(time, Time.NIGHT)) return Time.NIGHT;
        else return null;
    }

    private boolean isBetweenTimeSlice(TimeManager.Time time, TimeManager.Time slice) {
        TimeManager.Time lastSlice = getLastTimeSlice(slice);
        TimeManager.Time nextSlice = getNextTimeSlice(slice);

        return time.equals(lastSlice) || time.equals(slice) || time.equals(nextSlice);
    }

    public boolean validWakeTime(Time time) {
        if (Config.MORNING_CHECK.get().equals(Time.DISABLED)) return true;
        Time morningTime = Config.MORNING_CHECK.get();

        return isWithinPreviousSlices(time, morningTime);
    }

    public boolean isPlayerValid(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        String sleepTime = player.getData(CMData.SLEEPTIME);
        Time playerTime = getPlayerTimeSlice(serverPlayer);

        return !(sleepTime.equals("awake") || sleptLate(playerTime));
    }

    private boolean sleptLate(Time time) {
        if (Config.LATE_CHECK.get().equals(Time.DISABLED)) return false;
        Time lateTime = Config.LATE_CHECK.get();

        return isWithinFollowingSlices(time, lateTime);
    }

}