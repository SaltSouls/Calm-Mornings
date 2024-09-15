package salted.calmmornings.common.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;

public class TimeUtils {

    public static Time getTimeSlice(Level level) {
        Time timeChunk = getTimeChunk(level);
        if (timeChunk == null) return null; // this should never happen
        switch (timeChunk) {
            case MORNING -> { return determineTimeSlice(level, Time.MORNING); }
            case NOON -> { return determineTimeSlice(level, Time.NOON); }
            case EVENING -> { return determineTimeSlice(level, Time.EVENING); }
            case NIGHT -> { return determineTimeSlice(level, Time.NIGHT); }
            default -> {
                // this should never happen
                CalmMornings.LOGGER.error("""
                Time = null
                Some how you managed to make time null. This shouldn't be possible.
                Please let me know what you were doing when this occurred.""");
                return null;
            }
        }
    }

    public static Time getPlayerTimeSlice(ServerPlayer player) {
        ISleepTime sleepPlayer = SleepTime.get(player);
        String sleepTime = sleepPlayer.getSleepTime();

        switch (sleepTime) {
            case "early_morning" -> { return Time.MORNING_E; }
            case "morning" -> { return Time.MORNING; }
            case "late_morning" -> { return Time.MORNING_L; }
            case "early_afternoon" -> { return Time.NOON_E; }
            case "afternoon" -> { return Time.NOON; }
            case "late_afternoon" -> { return Time.NOON_L; }
            case "early_evening" -> { return Time.EVENING_E; }
            case "evening" -> { return Time.EVENING; }
            case "late_evening" -> { return Time.EVENING_L; }
            case "early_night" -> { return Time.NIGHT_E; }
            case "night" -> { return Time.NIGHT; }
            case "late_night" -> { return Time.NIGHT_L; }
            default -> { return null; } // this should never happen
        }
    }

    public static Time getPlayerTimeChunk(Time time) {
        if (isBetweenTimeSlice(time, Time.MORNING)) return Time.MORNING;
        else if (isBetweenTimeSlice(time, Time.NOON)) return Time.NOON;
        else if (isBetweenTimeSlice(time, Time.EVENING)) return Time.EVENING;
        else if (isBetweenTimeSlice(time, Time.NIGHT)) return Time.NIGHT;
        else return null;
    }

    public static boolean isWithinFollowingSlices(Time time, Time slice) {
        Time endSlice = getFollowingEnd(slice);
        return isBetweenTime(time, slice.start, endSlice.end);
    }

    // private methods for determining values
    private static final int dayLength = Level.TICKS_PER_DAY;

    private static boolean getTime(Level level, Time time) {
        if (level == null || time == null) return false; // this should never happen
        long start = time.getStart();
        long end = time.getEnd();
        /* apparently, this is how the game gets the time of day. don't know
         why it doesn't reset to 0 on waking or hitting 24000, but whatever. */
        long dayTime = level.getDayTime() % dayLength;
        CalmMornings.LOGGER.debug("current precise time: {}", dayTime);
        return dayTime >= start && dayTime < end;
    }

    private static boolean isBetweenTime(Time time, long start, long end) {
        return time.start >= start && time.end <= end;
    }

    private static Time getLastTimeSlice(Time time) {
        switch (time) {
            case MORNING_E -> { return Time.NIGHT_L; }
            case MORNING -> { return Time.MORNING_E; }
            case MORNING_L -> { return Time.MORNING; }
            case NOON_E -> { return Time.MORNING_L; }
            case NOON -> { return Time.NOON_E; }
            case NOON_L -> { return Time.NOON; }
            case EVENING_E -> { return Time.NOON_L; }
            case EVENING -> { return Time.EVENING_E; }
            case EVENING_L -> { return Time.EVENING; }
            case NIGHT_E -> { return Time.EVENING_L; }
            case NIGHT -> { return Time.NIGHT_E; }
            case NIGHT_L -> { return Time.NIGHT; }
            default -> { return null; } // this should never happen
        }
    }

    private static Time getNextTimeSlice(Time time) {
        switch (time) {
            case MORNING_E -> { return Time.MORNING; }
            case MORNING -> { return Time.MORNING_L; }
            case MORNING_L -> { return Time.NOON_E; }
            case NOON_E -> { return Time.NOON; }
            case NOON -> { return Time.NOON_L; }
            case NOON_L -> { return Time.EVENING_E; }
            case EVENING_E -> { return Time.EVENING; }
            case EVENING -> { return Time.EVENING_L; }
            case EVENING_L -> { return Time.NIGHT_E; }
            case NIGHT_E -> { return Time.NIGHT; }
            case NIGHT -> { return Time.NIGHT_L; }
            case NIGHT_L -> { return Time.MORNING_E; }
            default -> { return null; } // this should never happen
        }
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
        else return null;
    }

    private static Time determineTimeSlice(Level level, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        if (getTime(level, lastSlice)) return lastSlice;
        else if (getTime(level, slice)) return slice;
        else return nextSlice;
    }

    public enum Time {
        /*
        These calculations are based around a 24000 tick day
        and may not result in the same level of accuracy if
        the total ticks in a day are changed.
        */
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

}
