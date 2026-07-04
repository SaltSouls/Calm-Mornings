package salted.calmmornings.common.managers.utils;

import net.minecraft.world.level.Level;
import salted.calmmornings.CalmMornings;

public class TimeUtils {
    protected static final int dayLength = Level.TICKS_PER_DAY;

    private boolean getTime(Level level, Time time) {
        if (level == null || time == null) return false; // this should never happen
        long start = time.getStart();
        long end = time.getEnd();
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

    protected boolean isWithinPreviousSlices(Time time, Time slice) {
        return time.getEnd() <= slice.getEnd();
    }

    protected Time getLastTimeSlice(Time time) {
        return switch (time) {
            case MORNING_E  -> Time.NIGHT_L;
            case MORNING    -> Time.MORNING_E;
            case MORNING_L  -> Time.MORNING;
            case NOON_E     -> Time.MORNING_L;
            case NOON       -> Time.NOON_E;
            case NOON_L     -> Time.NOON;
            case EVENING_E  -> Time.NOON_L;
            case EVENING    -> Time.EVENING_E;
            case EVENING_L  -> Time.EVENING;
            case NIGHT_E    -> Time.EVENING_L;
            case NIGHT      -> Time.NIGHT_E;
            case NIGHT_L    -> Time.NIGHT;
            default -> timeError("time");
        };
    }

    protected boolean isWithinFollowingSlices(Time time, Time slice) {
        return time.getStart() >= slice.getStart();
    }

    protected Time getNextTimeSlice(Time time) {
        return switch (time) {
            case MORNING_E  -> Time.MORNING;
            case MORNING    -> Time.MORNING_L;
            case MORNING_L  -> Time.NOON_E;
            case NOON_E     -> Time.NOON;
            case NOON       -> Time.NOON_L;
            case NOON_L     -> Time.EVENING_E;
            case EVENING_E  -> Time.EVENING;
            case EVENING    -> Time.EVENING_L;
            case EVENING_L  -> Time.NIGHT_E;
            case NIGHT_E    -> Time.NIGHT;
            case NIGHT      -> Time.NIGHT_L;
            case NIGHT_L    -> Time.MORNING_E;
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
        MORNING_E, MORNING, MORNING_L,
        NOON_E,    NOON,    NOON_L,
        EVENING_E, EVENING, EVENING_L,
        NIGHT_E,   NIGHT,   NIGHT_L,
        DISABLED;   // only used for LATE_CHECK

        private static final int SEGMENT_LENGTH = dayLength / 12;

        public int getStart() {
            return this == DISABLED ? 0 : ordinal() * SEGMENT_LENGTH;
        }

        public int getEnd() {
            return this == DISABLED ? 0 : getStart() + SEGMENT_LENGTH;
        }
    }

}
