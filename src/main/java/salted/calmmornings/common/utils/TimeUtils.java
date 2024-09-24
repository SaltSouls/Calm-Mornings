package salted.calmmornings.common.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.registry.CMData;

public class TimeUtils {

    public static Time getTimeSlice(Level level) {
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

    public static Time getPlayerTimeSlice(ServerPlayer player) {
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

    public static Time getPlayerTimeChunk(Time time) {
        if (isBetweenTimeSlice(time, Time.MORNING)) return Time.MORNING;
        else if (isBetweenTimeSlice(time, Time.NOON)) return Time.NOON;
        else if (isBetweenTimeSlice(time, Time.EVENING)) return Time.EVENING;
        else if (isBetweenTimeSlice(time, Time.NIGHT)) return Time.NIGHT;
        else return null;
    }

    public static boolean isWithinPreviousSlices(Time time, Time slice) {
        Time startSlice = getPreviousStart(slice);
        return isBetweenTime(time, startSlice.start, slice.end);
    }

    public static boolean isWithinFollowingSlices(Time time, Time slice) {
        Time endSlice = getFollowingEnd(slice);
        return isBetweenTime(time, slice.start, endSlice.end);
    }

    public static boolean isBetweenTime(Time time, long start, long end) {
        return time.start >= start && time.end <= end;
    }

    // enums used for determining the current time the player slept/woke up
    public enum Time {
        /* These calculations are based around a 24000 tick day
         * and may not result in the same level of accuracy if
         * the total ticks in a day are changed. */
        MORNING_E(0, dayLength/12),                             // start:0      | end: 2,000
        MORNING(MORNING_E.getEnd(), dayLength / 6),                  // start:2,000  | end: 4,000
        MORNING_L(MORNING.getEnd(), dayLength / 4),                  // start:4,000  | end: 6,000
        NOON_E(MORNING_L.getEnd(), dayLength / 3),                   // start:6,000  | end: 8,000
        NOON(NOON_E.getEnd(), (int)(dayLength / 2.4)),                    // start:8,000  | end: 10,000
        NOON_L(NOON.getEnd(), dayLength / 2),                        // start:10,000 | end: 12,000
        EVENING_E(NOON_L.getEnd(), (int)(NOON_E.getEnd() * 1.75)),        // start:12,000 | end: 14,000
        EVENING(EVENING_E.getEnd(), NOON_E.getEnd() * 2),            // start:14,000 | end: 16,000
        EVENING_L(EVENING.getEnd(), (int)(NOON_L.getEnd() * 1.5)),        // start:16,000 | end: 18,000
        NIGHT_E(EVENING_L.getEnd(), NOON.getEnd() * 2),              // start:18,000 | end: 20,000
        NIGHT(NIGHT_E.getEnd(), (int)(EVENING.getEnd() * 1.375)),         // start:20,000 | end: 22,000
        NIGHT_L(NIGHT.getEnd(), dayLength),                               // start:22,000 | end: 24,000
        DISABLED(0, 0);                                         // only used for LATE_CHECK

        private final int start;
        private final int end;

        Time(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

    // private methods for determining values/conditions
    private static final int dayLength = Level.TICKS_PER_DAY;

    private static boolean getTime(Level level, Time time) {
        if (level == null || time == null) return false; // this should never happen
        long start = time.getStart();
        long end = time.getEnd();
        /* apparently, this is how the game gets the time of day. don't know
         * why it doesn't reset to 0 on waking or hitting 24000, but whatever. */
        long dayTime = level.getDayTime() % dayLength;
        return dayTime >= start && dayTime < end;
    }

    private static Time getLastTimeSlice(Time time) {
        return switch (time) {
            case MORNING_E -> Time.NIGHT_L;
            case MORNING -> Time.MORNING_E;
            case MORNING_L -> Time.MORNING;
            case NOON_E -> Time.MORNING_L;
            case NOON -> Time.NOON_E;
            case NOON_L -> Time.NOON;
            case EVENING_E -> Time.NOON_L;
            case EVENING -> Time.EVENING_E;
            case EVENING_L -> Time.EVENING;
            case NIGHT_E -> Time.EVENING_L;
            case NIGHT -> Time.NIGHT_E;
            case NIGHT_L -> Time.NIGHT;
            default -> timeError("time");
        };
    }

    private static Time getNextTimeSlice(Time time) {
        return switch (time) {
            case MORNING_E -> Time.MORNING;
            case MORNING -> Time.MORNING_L;
            case MORNING_L -> Time.NOON_E;
            case NOON_E -> Time.NOON;
            case NOON -> Time.NOON_L;
            case NOON_L -> Time.EVENING_E;
            case EVENING_E -> Time.EVENING;
            case EVENING -> Time.EVENING_L;
            case EVENING_L -> Time.NIGHT_E;
            case NIGHT_E -> Time.NIGHT;
            case NIGHT -> Time.NIGHT_L;
            case NIGHT_L -> Time.MORNING_E;
            default -> timeError("time");
        };
    }

    private static Time getPreviousStart(Time time) {
        if (time.equals(Time.MORNING_E)) return time;
        else return Time.MORNING_E;
    }

    private static Time getFollowingEnd(Time time) {
        if (time.equals(Time.NIGHT_L)) return time;
        else return Time.NIGHT_L;
    }

    private static boolean isBetweenTimeSlice(Level level, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        return getTime(level, lastSlice) || getTime(level, slice) || getTime(level, nextSlice);
    }

    private static boolean isBetweenTimeSlice(Time time, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        return time.equals(lastSlice) || time.equals(slice) || time.equals(nextSlice);
    }

    private static Time getTimeChunk(Level level) {
        if (isBetweenTimeSlice(level, Time.MORNING)) return Time.MORNING;
        else if (isBetweenTimeSlice(level, Time.NOON)) return Time.NOON;
        else if (isBetweenTimeSlice(level, Time.EVENING)) return Time.EVENING;
        else if (isBetweenTimeSlice(level, Time.NIGHT)) return Time.NIGHT;
        else return timeError("time");
    }

    private static Time determineTimeSlice(Level level, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        if (getTime(level, lastSlice)) return lastSlice;
        else if (getTime(level, slice)) return slice;
        else return nextSlice;
    }

    // error handling
    private static Time timeError(String type) {
        CalmMornings.LOGGER.error("""
                Somehow you managed to make {} null. This shouldn't be possible.
                Please let me know what you were doing when this occurred.""", type);
        return null;
    }

}
