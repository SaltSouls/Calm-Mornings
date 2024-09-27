package salted.calmmornings.common.managers.utils;

import net.minecraft.world.level.Level;
import salted.calmmornings.CalmMornings;

public class TimeUtils {
    protected static final int dayLength = Level.TICKS_PER_DAY;

    private boolean getTime(Level level, Time time) {
        if (level == null || time == null) return false; // this should never happen
        long start = time.start;
        long end = time.end;
        /* apparently, this is how the game gets the time of day. don't know
         * why it doesn't reset to 0 on waking or hitting 24000, but whatever. */
        long dayTime = level.getDayTime() % dayLength;
        return dayTime >= start && dayTime < end;
    }

    protected Time getTimeChunk(Level level) {
        if (isBetweenTimeSlice(level, Time.MORNING)) return Time.MORNING;
        else if (isBetweenTimeSlice(level, Time.NOON)) return Time.NOON;
        else if (isBetweenTimeSlice(level, Time.EVENING)) return Time.EVENING;
        else if (isBetweenTimeSlice(level, Time.NIGHT)) return Time.NIGHT;
        else return timeError("time");
    }

    private boolean isBetweenTimeSlice(Level level, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        return getTime(level, lastSlice) || getTime(level, slice) || getTime(level, nextSlice);
    }

    private boolean isBetweenTime(Time time, long start, long end) {
        return time.start >= start && time.end <= end;
    }

    protected boolean isWithinPreviousSlices(Time time, Time slice) {
        Time startSlice = time.equals(Time.MORNING_E) ? time : Time.MORNING_E;
        return isBetweenTime(time, startSlice.start, slice.end);
    }

    protected Time getLastTimeSlice(Time time) {
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

    protected boolean isWithinFollowingSlices(Time time, Time slice) {
        Time endSlice = time.equals(Time.NIGHT_L) ? time : Time.NIGHT_L;
        return isBetweenTime(time, slice.start, endSlice.end);
    }

    protected Time getNextTimeSlice(Time time) {
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

    protected Time determineTimeSlice(Level level, Time slice) {
        Time lastSlice = getLastTimeSlice(slice);
        Time nextSlice = getNextTimeSlice(slice);

        if (getTime(level, lastSlice)) return lastSlice;
        else if (getTime(level, slice)) return slice;
        else return nextSlice;
    }

    // error handling
    protected Time timeError(String type) {
        CalmMornings.LOGGER.error("""
                Somehow you managed to make {} null. This shouldn't be possible.
                Please let me know what you were doing when this occurred.""", type);
        return null;
    }

    // enums used for determining the current time the player slept/woke up
    public enum Time {
        /* These calculations are based around a 24000 tick day
         * and may not result in the same level of accuracy if
         * the total ticks in a day are changed. */
        MORNING_E(0, dayLength / 12),                             // start:0      | end: 2,000
        MORNING(MORNING_E.end, dayLength / 6),                  // start:2,000  | end: 4,000
        MORNING_L(MORNING.end, dayLength / 4),                  // start:4,000  | end: 6,000
        NOON_E(MORNING_L.end, dayLength / 3),                   // start:6,000  | end: 8,000
        NOON(NOON_E.end, (int) (dayLength / 2.4)),                    // start:8,000  | end: 10,000
        NOON_L(NOON.end, dayLength / 2),                        // start:10,000 | end: 12,000
        EVENING_E(NOON_L.end, (int) (NOON_E.end * 1.75)),        // start:12,000 | end: 14,000
        EVENING(EVENING_E.end, NOON_E.end * 2),            // start:14,000 | end: 16,000
        EVENING_L(EVENING.end, (int) (NOON_L.end * 1.5)),        // start:16,000 | end: 18,000
        NIGHT_E(EVENING_L.end, NOON.end * 2),              // start:18,000 | end: 20,000
        NIGHT(NIGHT_E.end, (int) (EVENING.end * 1.375)),         // start:20,000 | end: 22,000
        NIGHT_L(NIGHT.end, dayLength),                               // start:22,000 | end: 24,000
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
