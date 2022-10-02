package com.tainted.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static int radius;
    public static int height;
    public static double anticheese;

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == Config.COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static void bakeConfig() {
        radius = COMMON.radius.get();
        height = COMMON.height.get();
    }

    public static class Common {

        public final ForgeConfigSpec.IntValue radius;
        public final ForgeConfigSpec.IntValue height;
        public final ForgeConfigSpec.DoubleValue anticheese;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Calm Mornings Config");

            radius = builder.comment("""
                    Base horizontal radius(in blocks) in which enemies despawn around player upon waking.
                    Difficulty will effect the max range in which it checks.
                    EASY: base | NORMAL: base / 2 | HARD: base / 4 | Default: 64""")
                    .translation(CalmMornings.MODID + ".config." + "radius")
                    .defineInRange("radius", () -> 64, 0, 256);
            height = builder.comment("""
                    Base vertical radius(in blocks) in which enemies despawn around player upon waking.
                    Difficulty will effect the max range in which it checks.
                    EASY: base | NORMAL: base / 2 | HARD: base / 4 | Default: 16""")
                    .translation(CalmMornings.MODID + ".config." + "height")
                    .defineInRange("height", () -> 16, 0, 64);
            anticheese = builder.comment("""
                    Checks for a radius near the player to see if mobs are considered too close to be removed.
                    Difficulty will effect the max range in which it checks.
                    EASY: Disabled | NORMAL: base | HARD: base * 2 | Default: 2.5""")
                    .translation(CalmMornings.MODID + ".config." + "anticheese")
                    .defineInRange("anti-cheese", () -> 2.5, 0, 25);

            builder.pop();
        }
    }

}
