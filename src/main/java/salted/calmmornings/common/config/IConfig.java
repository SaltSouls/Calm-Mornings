package salted.calmmornings.common.config;

import salted.calmmornings.common.utils.TimeUtils.Time;

import java.util.List;

import static salted.calmmornings.common.config.Config.COMMON;

public interface IConfig {

    static boolean getEnableList() {
        return COMMON.ENABLE_LIST.get();
    }

    static boolean isBlacklist() {
        return COMMON.IS_BLACKLIST.get();
    }

    static List<? extends String> getMobList() {
        return COMMON.MOB_LIST.get();
    }

    static List<? extends String> getCategoryList() {
        return COMMON.MOBCATEGORY_LIST.get();
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

    static Time getLateCheck() {
        return COMMON.LATE_CHECK.get();
    }

    static Time getMorningCheck() {
        return COMMON.MORNING_CHECK.get();
    }

    static boolean getEnablePlayerCheck() {
        return COMMON.PLAYER_CHECK.get();
    }

    static boolean getMobCheck() {
        return COMMON.MOB_CHECK.get();
    }

    static boolean getBetterChecking() {
        return COMMON.BETTER_CHECKING.get();
    }

    static boolean getMonsterCheck() {
        return COMMON.MONSTER.get();
    }

    static boolean getCreatureCheck() {
        return COMMON.CREATURE.get();
    }

    static boolean getAxolotlsCheck() {
        return COMMON.AXOLOTLS.get();
    }

    static boolean getWaterCreatureCheck() {
        return COMMON.WATER_CREATURE.get();
    }

    static boolean getUndergroundWaterCreatureCheck() {
        return COMMON.UNDERGROUND_WATER_CREATURE.get();
    }

    static boolean getAmbientCheck() {
        return COMMON.AMBIENT.get();
    }

    static boolean getWaterAmbientCheck() {
        return COMMON.WATER_AMBIENT.get();
    }

    static boolean getMiscCheck() {
        return COMMON.MISC.get();
    }
    
}
