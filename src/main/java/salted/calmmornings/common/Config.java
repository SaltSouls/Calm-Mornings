package salted.calmmornings.common;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.utils.TimeUtils.Time;

import java.util.ArrayList;
import java.util.List;


public class Config {
    public static ModConfigSpec COMMON_CONFIG;
    private static final List<String> defaultList = new ArrayList<>(List.of("minecraft:creeper", "minecraft:zombie", "minecraft:spider"));
    public static ModConfigSpec.BooleanValue ENABLE_LIST;
    public static ModConfigSpec.BooleanValue IS_BLACKLIST;
    public static ModConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;
    public static ModConfigSpec.BooleanValue ENABLE_SCALING;
    public static ModConfigSpec.IntValue VERTICAL_RANGE;
    public static ModConfigSpec.IntValue HORIZONTAL_RANGE;
    public static ModConfigSpec.EnumValue<Time> LATE_CHECK;
    public static ModConfigSpec.EnumValue<Time> MORNING_CHECK;
    public static ModConfigSpec.BooleanValue PLAYER_CHECK;
    public static ModConfigSpec.BooleanValue MOB_CHECK;
    public static ModConfigSpec.BooleanValue BETTER_CHECKING;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        String modid = CalmMornings.MODID;
        String CATEGORY_GENERAL = "general";
        builder.comment("General Settings").translation(modid + ".config." + "CATEGORY_GENERAL").push(CATEGORY_GENERAL);
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
                .translation(modid + ".config." + "MOB_LIST").defineListAllowEmpty(List.of("mobs"), () -> defaultList, () -> "", mobs -> mobs instanceof String);
        builder.pop();

        String CATEGORY_RANGE = "range";
        builder.comment("Range Settings").translation(modid + ".config." + "CATEGORY_RANGE").push(CATEGORY_RANGE);
        ENABLE_SCALING = builder
                .comment("""
                        Should difficulty based range scaling be enabled?
                        Difficulty Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4""")
                .translation(modid + ".config." + "ENABLED_SCALING")
                .define("enableScaling", true);

        HORIZONTAL_RANGE = builder
                .comment("Horizontal radius to check for mobs to despawn.")
                .translation(modid + ".config." + "HORIZONTAL_RANGE")
                .defineInRange("horizontalRange", () -> 64, 0, 256);

        VERTICAL_RANGE = builder
                .comment("Vertical radius to check for mobs to despawn.")
                .translation(modid + ".config." + "VERTICAL_RANGE")
                .defineInRange("verticalRange", () -> 16, 0, 64);
        builder.pop();

        String CATEGORY_CHECKS = "checks";
        builder.comment("Conditional Checks").translation(modid + ".config." + "CATEGORY_CHECKS").push(CATEGORY_CHECKS);
        LATE_CHECK = builder
                .comment("Latest time a player can sleep to allow despawning.")
                .translation(modid + ".config." + "LATE_CHECK")
                .defineEnum("lateCheck", Time.NIGHT_L);

        MORNING_CHECK = builder
                .comment("Latest time a player can wakeup to allow despawning.")
                .translation(modid + ".config." + "MORNING_CHECK")
                .defineEnum("morningCheck", Time.MORNING_E);

        PLAYER_CHECK = builder
                .comment("Should non-sleeping players prevent despawning around them?")
                .translation(modid + ".config." + "ENABLE_PLAYER_CHECK")
                .define("playerCheck", true);

        MOB_CHECK = builder
                .comment("Should nearby monsters prevent sleep?")
                .translation(modid + ".config." + "MOB_CHECK")
                .define("monsterCheck", true);

        BETTER_CHECKING = builder
                .comment("Should only monsters tracking the player prevent sleep? Requires monsterCheck.")
                .translation(modid + ".config." + "BETTER_CHECKING")
                .define("betterChecking", true);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}