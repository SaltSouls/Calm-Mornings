package salted.calmmornings.common.config;

import salted.calmmornings.common.managers.utils.TimeUtils.Time;

import java.util.HashSet;
import java.util.Map;

import static salted.calmmornings.common.config.Config.COMMON;

public interface IConfig {

    static boolean getEnableList() {
        return COMMON.ENABLE_LIST.get();
    }

    static HashSet<String> getMobSet() {
        return COMMON.MOB_SET;
    }

    static Map<String, String> getMobGroupMap() {
        return COMMON.MOB_GROUP_MAP;
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

    static boolean getPlayerCheck() {
        return COMMON.PLAYER_CHECK.get();
    }

    static boolean getBossCheck() {
        return COMMON.BOSS_CHECK.get();
    }

    static boolean getMonsterCheck() {
        return COMMON.MONSTER_CHECK.get();
    }

    static boolean getVillagerCheck() {
        return COMMON.VILLAGER_CHECK.get();
    }

    static boolean getCreatureCheck() {
        return COMMON.CREATURE_CHECK.get();
    }

    static boolean getAmbientCheck() {
        return COMMON.AMBIENT_CHECK.get();
    }

    static boolean getConstructCheck() {
        return COMMON.CONSTRUCT_CHECK.get();
    }

    static boolean getMiscCheck() {
        return COMMON.MISC_CHECK.get();
    }

}
