package com.tainted.common.config;

import com.tainted.CalmMornings;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CalmMornings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final Builder BUILDER = new Builder();
    public static final Common COMMON = new Common(BUILDER);
    public static final ForgeConfigSpec COMMON_SPEC = BUILDER.build();

    public static class Common {

        private static final List<String> defaultList = new ArrayList<>(List.of("minecraft:creeper", "minecraft:zombie", "minecraft:spider"));
        public final String CATEGORY_GENERAL = "general";
        public final IntValue SLEEP_TIMER;
        public final BooleanValue LATE_CHECK;
        public final BooleanValue PLAYER_CHECK;
        public final BooleanValue ENABLE_LIST;
        public final ConfigValue<List<? extends String>> MOB_LIST;
        public final String CATEGORY_RANGES = "ranges";
        public final BooleanValue ENABLE_SCALING;
        public final IntValue VERTICAL_RANGE;
        public final IntValue HORIZONTAL_RANGE;
        public final DoubleValue ANTI_CHEESE;

        public Common(@NotNull Builder builder) {
            String modid = CalmMornings.MOD_ID;
            builder.comment("General Settings").push(CATEGORY_GENERAL);
            SLEEP_TIMER = builder
                    .comment("""
                            How long does the player need to be sleeping in order
                            for entity despawning to occur? | Default: 20""")
                    .translation(modid + ".config." + "SLEEP_TIMER")
                    .defineInRange("sleepTimer", 20, 1, 100);
            LATE_CHECK = builder
                    .comment("Check to see if it's too close to morning? | Default: true")
                    .translation(modid + ".config." + "LATE_CHECK")
                    .define("sleptLateCheck", true);
            PLAYER_CHECK = builder
                    .comment("""
                            Should check and disable entity despawning around other
                            non-sleeping players withing range? | Default: true""")
                    .translation(modid + ".config." + "ENABLE_PLAYER_CHECK")
                    .define("playerCheck", true);
            ENABLE_LIST = builder
                    .comment("Enables individual mob despawns instead of group despawning. | Default: false")
                    .translation(modid + ".config." + "ENABLE_LIST")
                    .define("enableList", false);
            MOB_LIST = builder
                    .comment("""
                            List of mobs to despawn.
                            Formatting: ["minecraft:creeper", "minecraft:zombie", "minecraft:spider", "modid:entityname"]""")
                    .translation(modid + ".config." + "MOB_LIST")
                    .defineListAllowEmpty(List.of("mobs"), () -> defaultList, entity -> (entity instanceof String string && ResourceLocation.isValidResourceLocation(string)));
            builder.pop();

            builder.comment("Range Settings").push(CATEGORY_RANGES);
            ENABLE_SCALING = builder
                    .comment("Should scaling based on difficulty be enabled? | Default: true")
                    .translation(modid + ".config." + "ENABLED_SCALING")
                    .define("enableScaling", true);
            VERTICAL_RANGE = builder
                    .comment("""
                            Vertical range to check for mobs to despawn. | Default: 16
                            Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(modid + ".config." + "VERTICAL_RANGE")
                    .defineInRange("verticalRange", () -> 16, 0, 64);
            HORIZONTAL_RANGE = builder
                    .comment("""
                            Horizontal range to check for mobs to despawn. | Default: 64
                            Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(modid + ".config." + "HORIZONTAL_RANGE")
                    .defineInRange("horizontalRange", () -> 64, 0, 256);
            ANTI_CHEESE = builder
                    .comment("""
                            Range in which mobs are considered too close to despawn.
                            Disabled if scaling isn't enabled or when set to 0. | Default: 2.5
                            Scaling: EASY = Disabled | NORMAL = base | HARD = base * 2""")
                    .translation(modid + ".config." + "ANTI_CHEESE")
                    .defineInRange("antiCheese", () -> 2.5D, 0.0D, 5.0D);
            builder.pop();
        }
    }

}
