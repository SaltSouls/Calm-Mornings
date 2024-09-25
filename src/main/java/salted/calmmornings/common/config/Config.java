package salted.calmmornings.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.utils.TimeUtils.Time;

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
        private static final List<String> defaultMobList = new ArrayList<>(List.of("minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:creeper"));
        private static final List<String> defaultCategoryList = new ArrayList<>(List.of("minecraft:villager:creature", "minecraft:iron_golem:creature", "minecraft:snow_golem:creature"));

        public final ForgeConfigSpec.BooleanValue ENABLE_LIST;
        public final ForgeConfigSpec.BooleanValue IS_BLACKLIST;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> MOBCATEGORY_LIST;
        public final ForgeConfigSpec.BooleanValue ENABLE_SCALING;
        public final ForgeConfigSpec.IntValue VERTICAL_RANGE;
        public final ForgeConfigSpec.IntValue HORIZONTAL_RANGE;
        public final ForgeConfigSpec.EnumValue<Time> LATE_CHECK;
        public final ForgeConfigSpec.EnumValue<Time> MORNING_CHECK;
        public final ForgeConfigSpec.BooleanValue PLAYER_CHECK;
        public final ForgeConfigSpec.BooleanValue MOB_CHECK;
        public final ForgeConfigSpec.BooleanValue BETTER_CHECKING;
        public final ForgeConfigSpec.BooleanValue MONSTER;
        public final ForgeConfigSpec.BooleanValue CREATURE;
        public final ForgeConfigSpec.BooleanValue AXOLOTLS;
        public final ForgeConfigSpec.BooleanValue WATER_CREATURE;
        public final ForgeConfigSpec.BooleanValue UNDERGROUND_WATER_CREATURE;
        public final ForgeConfigSpec.BooleanValue AMBIENT;
        public final ForgeConfigSpec.BooleanValue WATER_AMBIENT;
        public final ForgeConfigSpec.BooleanValue MISC;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
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
                        List of mobs to despawn. '*' adds all entities in modId. Requires enableList.
                        Formatting: ["minecraft:zombie", "minecraft:skeleton", "<modId>:<entityId>"]""")
                    .translation(modid + ".config." + "MOB_LIST").defineListAllowEmpty(List.of("mobs"), () -> defaultMobList, mobs -> mobs instanceof String);

            MOBCATEGORY_LIST = builder
                    .comment("""
                        Change mob's viewed MobCategory when despawning. '*' adds all entities in modId.
                        Formatting: ["minecraft:villager:creature", "<modId>:<entityId>:<mobCategory>"]
                        Allowed Categories: [monster, creature, water_creature, underground_water_creature, ambient, water_ambient, misc]""")
                    .translation(modid + ".config." + "MOBCATEGORY_LIST").defineListAllowEmpty(List.of("changed"), () -> defaultCategoryList, mobs -> mobs instanceof String);
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
                    .comment("Player must sleep before this time to allow despawning.")
                    .translation(modid + ".config." + "LATE_CHECK")
                    .defineEnum("lateCheck", Time.NIGHT_L);

            MORNING_CHECK = builder
                    .comment("Latest time the player can wakeup to allow despawning.")
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

            String CATEGORY_MOBCATEGORY_CHECKS = "category_checks";
            builder.comment("Allow listed MobCategories when despawning? Requires enableList.")
                    .translation(modid + ".config." + "CATEGORY_MOBCATEGORY_CHECKS")
                    .push(CATEGORY_MOBCATEGORY_CHECKS);

            MONSTER = builder
                    .comment("Enable MONSTER check?")
                    .translation(modid + ".config." + "MONSTER")
                    .define("MONSTER", true);

            CREATURE = builder
                    .comment("Enable CREATURE check?")
                    .translation(modid + ".config." + "CREATURE")
                    .define("CREATURE", true);

            AXOLOTLS = builder
                    .comment("Enable AXOLOTLS check?")
                    .translation(modid + ".config." + "AXOLOTLS")
                    .define("AXOLOTLS", true);

            WATER_CREATURE = builder
                    .comment("Enable WATER_CREATURE check?")
                    .translation(modid + ".config." + "WATER_CREATURE")
                    .define("WATER_CREATURE", true);

            UNDERGROUND_WATER_CREATURE = builder
                    .comment("Enable UNDERGROUND_WATER_CREATURE check?")
                    .translation(modid + ".config." + "UNDERGROUND_WATER_CREATURE")
                    .define("UNDERGROUND_WATER_CREATURE", true);

            AMBIENT = builder
                    .comment("Enable AMBIENT check?")
                    .translation(modid + ".config." + "AMBIENT")
                    .define("AMBIENT", true);

            WATER_AMBIENT = builder
                    .comment("Enable WATER_AMBIENT check?")
                    .translation(modid + ".config." + "WATER_AMBIENT")
                    .define("WATER_AMBIENT", true);

            MISC = builder
                    .comment("Enable MISC check?")
                    .translation(modid + ".config." + "MISC")
                    .define("MISC", false);
            builder.pop();
        }
    }

}