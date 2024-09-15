package salted.calmmornings.common.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.util.TimeUtils.Time;

import java.util.ArrayList;
import java.util.List;


public class Config implements IConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static class CommonConfig {
        private static final List<String> defaultList = new ArrayList<>(List.of("minecraft:creeper", "minecraft:zombie", "minecraft:spider"));

        public final ForgeConfigSpec.EnumValue<Time> LATE_CHECK;
        public final ForgeConfigSpec.BooleanValue MOB_CHECK;
        public final ForgeConfigSpec.BooleanValue BETTER_CHECKING;
        public final ForgeConfigSpec.BooleanValue PLAYER_CHECK;
        public final ForgeConfigSpec.BooleanValue ENABLE_LIST;
        public final ForgeConfigSpec.BooleanValue IS_BLACKLIST;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;
        public final ForgeConfigSpec.BooleanValue ENABLE_SCALING;
        public final ForgeConfigSpec.IntValue VERTICAL_RANGE;
        public final ForgeConfigSpec.IntValue HORIZONTAL_RANGE;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            String modid = CalmMornings.MODID;
            String CATEGORY_GENERAL = "general";
            builder.comment("General Settings").push(CATEGORY_GENERAL);
            ENABLE_LIST = builder
                    .comment("Use list instead of mobCategory for despawning?")
                    .translation(modid + ".config." + "ENABLE_LIST")
                    .define("enableList", false);

            IS_BLACKLIST = builder
                    .comment("Changes the list to be a blacklist. Requires enableList.")
                    .translation( modid + ".config." + "IS_BLACKLIST")
                    .define("isBlacklist", false);

            MOB_LIST = builder
                    .comment("""
                            List of mobs to despawn. Requires enableList.
                            Formatting: ["minecraft:creeper", "minecraft:zombie", "minecraft:spider", "modID:entityID"]""")
                    .translation(modid + ".config." + "MOB_LIST")
                    .defineListAllowEmpty(List.of("mobs"), () -> defaultList, entity -> (entity instanceof String string && ResourceLocation.isValidResourceLocation(string)));
            builder.pop();

            String CATEGORY_RANGE = "range";
            builder.comment("Range Settings").push(CATEGORY_RANGE);
            ENABLE_SCALING = builder
                    .comment("""
                            Should difficulty based range scaling be enabled?
                            Difficulty Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                    .translation(modid + ".config." + "ENABLED_SCALING")
                    .define("enableScaling", true);

            VERTICAL_RANGE = builder
                    .comment("Vertical radius to check for mobs to despawn.")
                    .translation(modid + ".config." + "VERTICAL_RANGE")
                    .defineInRange("verticalRange", () -> 16, 0, 64);

            HORIZONTAL_RANGE = builder
                    .comment("Horizontal radius to check for mobs to despawn.")
                    .translation(modid + ".config." + "HORIZONTAL_RANGE")
                    .defineInRange("horizontalRange", () -> 64, 0, 256);
            builder.pop();

            String CATEGORY_CHECKS = "checks";
            builder.comment("Conditional Checks").push(CATEGORY_CHECKS);
            LATE_CHECK = builder
                    .comment("Latest time a player can sleep to allow despawning.")
                    .translation(modid + ".config." + "LATE_CHECK")
                    .defineEnum("lateCheck", Time.NIGHT_L);

            PLAYER_CHECK = builder
                    .comment("Should non-sleeping players prevent despawning around them?")
                    .translation(modid + ".config." + "ENABLE_PLAYER_CHECK")
                    .define("playerCheck", true);

            MOB_CHECK = builder
                    .comment("Should nearby monsters prevent sleep?")
                    .translation(modid + ".config." + "DISABLE_CHECK")
                    .define("monsterCheck", true);

            BETTER_CHECKING = builder
                    .comment("Should only monsters tracking the player prevent sleep? Requires enableMobCheck.")
                    .translation(modid + ".config." + "BETTER_CHECKING")
                    .define("betterChecking", true);
            builder.pop();
        }
    }
}