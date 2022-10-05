package com.tainted.common.utils;

import com.tainted.common.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

public class SleepUtils {

    public static boolean shouldDespawn(Entity entity) {
        EntityType<?> type = entity.getType();
        if(Config.ENABLE_LIST) {
            String mobKey = EntityType.getKey(type).toString();
            return Config.MOB_LIST.contains(mobKey);
        } else if(type != EntityType.ENDER_DRAGON || type != EntityType.WITHER
                || type != EntityType.ELDER_GUARDIAN || type != EntityType.GUARDIAN) {
            return type.getCategory() == MobCategory.MONSTER;
        }
        return false;
    }

    public static boolean isDay(Level level) {
        long getTime = level.getDayTime();
        return getTime % 24000 < 100;
    }
}
