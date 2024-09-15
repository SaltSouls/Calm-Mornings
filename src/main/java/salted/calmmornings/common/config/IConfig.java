package salted.calmmornings.common.config;

import salted.calmmornings.common.util.TimeUtils.Time;

import java.util.List;

import static salted.calmmornings.common.config.Config.COMMON;

public interface IConfig {

    static Enum<Time> getLateCheck() {return COMMON.LATE_CHECK.get();}

    static boolean getMobCheck() {
        return COMMON.MOB_CHECK.get();
    }

    static boolean getBetterChecking() {
        return COMMON.BETTER_CHECKING.get();
    }

    static boolean getEnablePlayerCheck() {
        return COMMON.PLAYER_CHECK.get();
    }

    static boolean getEnableList() {
        return COMMON.ENABLE_LIST.get();
    }

    static boolean isBlacklist() {
        return COMMON.IS_BLACKLIST.get();
    }

    static List<? extends String> getMobList() {
        return COMMON.MOB_LIST.get();
    }

    static boolean getEnableScaling() {
        return COMMON.ENABLE_SCALING.get();
    }

    static int getVerticalRange() {
        return COMMON.VERTICAL_RANGE.get();
    }

    static int getHorizontalRange() {
        return COMMON.HORIZONTAL_RANGE.get();
    }
    
}
