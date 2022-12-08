package com.tainted.common.config;

import java.util.List;

import static com.tainted.common.config.Config.COMMON;

public interface ConfigHelper {

    static int getSleepTimer() { return COMMON.SLEEP_TIMER.get(); }
    static boolean getEnableLateCheck() { return COMMON.LATE_CHECK.get(); }
    static boolean getEnablePlayerCheck() { return COMMON.PLAYER_CHECK.get(); }
    static boolean getEnableList() { return COMMON.ENABLE_LIST.get(); }
    static List<? extends String> getMobList() { return COMMON.MOB_LIST.get(); }
    static boolean getEnableScaling() { return COMMON.ENABLE_SCALING.get(); }
    static int getVerticalRange() { return COMMON.VERTICAL_RANGE.get(); }
    static int getHorizontalRange() { return COMMON.HORIZONTAL_RANGE.get(); }
    static double getAntiCheese() { return COMMON.ANTI_CHEESE.get(); }

}