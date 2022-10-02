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
                    Base horizontal radius(in blocks) in which enemies despawn around player upon waking | Default: 128
                    Difficulty will effect the max radius in which the entities despawn. See below:
                    EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(CalmMornings.MODID + ".config." + "radius")
                    .defineInRange("radius", () -> 128, 0, 2048);
            height = builder.comment("""
                    Base vertical radius(in blocks) in which enemies despawn around player upon waking | Default: 32
                    Difficulty will effect the max radius in which the entities despawn. See below:
                    Only effects EASY difficulty. NORMAL = 16 | HARD = 0""")
                    .translation(CalmMornings.MODID + ".config." + "height")
                    .defineInRange("height", () -> 32, 0, 384);
            anticheese = builder.comment("""
                    Checks for a radius near the player to see if mobs are considered too close to be removed. | Default: 2.5
                    Difficulty will effect the max radius in which it checks. See below:
                    NORMAL = base | HARD = base * 2""")
                    .translation(CalmMornings.MODID + ".config." + "anticheese")
                    .defineInRange("anti cheese radius", () -> 2.5, 0, 25);

            builder.pop();
        }
    }

}
