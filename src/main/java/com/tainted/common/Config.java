package com.tainted.common;

import com.google.common.collect.ImmutableList;
import com.tainted.CalmMornings;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Mod.EventBusSubscriber(modid = CalmMornings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static boolean ENABLE_SCALING;
    public static int VERTICAL_RANGE;
    public static int HORIZONTAL_RANGE;
    public static double ANTI_CHEESE;
    public static boolean ENABLE_LIST;
    public static List<? extends String> MOB_LIST;

    public static void bakeConfig() {
        ENABLE_SCALING = COMMON.ENABLE_SCALING.get();
        VERTICAL_RANGE = COMMON.VERTICAL_RANGE.get();
        HORIZONTAL_RANGE = COMMON.HORIZONTAL_RANGE.get();
        ANTI_CHEESE = COMMON.ANTI_CHEESE.get();
        ENABLE_LIST = COMMON.ENABLE_LIST.get();
        MOB_LIST = COMMON.MOB_LIST.get();
    }

    public static class Common {
        public final String CATEGORY_VALUES = "settings";
        public final ForgeConfigSpec.BooleanValue ENABLE_SCALING;
        public final ForgeConfigSpec.IntValue VERTICAL_RANGE;
        public final ForgeConfigSpec.IntValue HORIZONTAL_RANGE;
        public final ForgeConfigSpec.DoubleValue ANTI_CHEESE;
        public final String CATEGORY_LIST = "list";
        public final ForgeConfigSpec.BooleanValue ENABLE_LIST;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Range Settings").push(CATEGORY_VALUES);
            ENABLE_SCALING = builder.comment("Should scaling based on difficulty be enabled? | Default: true")
                    .translation(CalmMornings.MOD_ID + ".config." + "ENABLED_SCALING")
                    .define("enableScaling", true);
            VERTICAL_RANGE = builder.comment("""
                    Vertical range to check for mobs to despawn. | Default: 16
                    Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(CalmMornings.MOD_ID + ".config." + "VERTICAL_RANGE")
                    .defineInRange("verticalRange", () -> 16, 0, 64);
            HORIZONTAL_RANGE = builder.comment("""
                    Horizontal range to check for mobs to despawn. | Default: 64
                    Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(CalmMornings.MOD_ID + ".config." + "HORIZONTAL_RANGE")
                    .defineInRange("horizontalRange", () -> 64, 0, 256);
            ANTI_CHEESE = builder.comment("""
                    Range in which mobs are considered too close to despawn. Disabled if scaling isn't enabled. | Default: 2.5
                    Scaling: EASY = Disabled | NORMAL = base | HARD = base * 2""")
                    .translation(CalmMornings.MOD_ID + ".config." + "ANTI_CHEESE")
                    .defineInRange("antiCheese", () -> 2.5, 0, 25);
            builder.pop();

            builder.comment("List Settings").push(CATEGORY_LIST);
            ENABLE_LIST = builder.comment("Enables individual mob despawns instead of group despawning. | Default: false")
                    .translation(CalmMornings.MOD_ID + ".config." + "ENABLE_LIST")
                    .define("enableList", false);
            MOB_LIST = builder.comment("List of mobs to despawn. | Formatting: [\"minecraft:creeper\", \"modid:entityname\"]")
                    .translation(CalmMornings.MOD_ID + ".config." + "MOB_LIST")
                    .defineList("mobList", ImmutableList.of("minecraft:creeper"), obj -> true);
            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == Config.COMMON_SPEC) {
            bakeConfig();
        }
    }

}
