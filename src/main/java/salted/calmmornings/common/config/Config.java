package salted.calmmornings.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.managers.utils.TimeUtils.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Config implements IConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON = specPair.getLeft();
        COMMON_SPEC = specPair.getRight();
    }

    public static void setupDespawnLists() {
        // clear entries
        COMMON.MOB_GROUP_MAP.clear();
        COMMON.MOB_SET.clear();

        // build maps
        if (!COMMON.MOB_GROUP_LIST.get().isEmpty()) {
            COMMON.MOB_GROUP_LIST.get().forEach(mob -> {
                String[] split = mob.split(":");
                if (split.length < 3) {
                    CalmMornings.LOGGER.error("[{}] does not contain a valid mob category!", mob);
                    return;
                }
                COMMON.MOB_GROUP_MAP.put(split[0] + ":" + split[1], split[2]);
            });
        }

        if (COMMON.MOB_LIST.get().isEmpty()) return;
        COMMON.MOB_SET.addAll(COMMON.MOB_LIST.get());
    }

    public static class CommonConfig {
        private static final List<String> defaultMobList = new ArrayList<>(List.of(
                "minecraft:zombie",
                "minecraft:skeleton",
                "minecraft:spider",
                "minecraft:creeper"
        ));
        private static final List<? extends String> defaultMobGroupList = new ArrayList<>(List.of(
                "minecraft:ender_dragon:boss",
                "minecraft:wither:boss",
                "minecraft:warden:boss",
                "minecraft:villager:villager",
                "minecraft:wandering_trader:villager",
                "minecraft:iron_golem:construct",
                "minecraft:snow_golem:construct"
        ));
        public final HashSet<String> MOB_SET = new HashSet<>();
        public final HashMap<String, String> MOB_GROUP_MAP = new HashMap<>();

        public final ForgeConfigSpec.BooleanValue ENABLE_LIST;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_GROUP_LIST;
        public final ForgeConfigSpec.BooleanValue ENABLE_SCALING;
        public final ForgeConfigSpec.IntValue VERTICAL_RANGE;
        public final ForgeConfigSpec.IntValue HORIZONTAL_RANGE;
        public final ForgeConfigSpec.EnumValue<Time> LATE_CHECK;
        public final ForgeConfigSpec.EnumValue<Time> MORNING_CHECK;
        public final ForgeConfigSpec.BooleanValue PLAYER_CHECK;
        public final ForgeConfigSpec.BooleanValue BOSS_CHECK;
        public final ForgeConfigSpec.BooleanValue MONSTER_CHECK;
        public final ForgeConfigSpec.BooleanValue VILLAGER_CHECK;
        public final ForgeConfigSpec.BooleanValue CREATURE_CHECK;
        public final ForgeConfigSpec.BooleanValue AMBIENT_CHECK;
        public final ForgeConfigSpec.BooleanValue CONSTRUCT_CHECK;
        public final ForgeConfigSpec.BooleanValue MISC_CHECK;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            String modid = CalmMornings.MODID;
            String CATEGORY_GENERAL = "general";
            builder.comment("General Settings").translation(modid + ".config." + "CATEGORY_GENERAL").push(CATEGORY_GENERAL);
            ENABLE_LIST = builder
                    .comment("Use list instead of builtin rules for despawning?")
                    .translation(modid + ".config." + "ENABLE_LIST")
                    .define("enableList", false);

            MOB_LIST = builder
                    .comment("""
                            List of mobs to despawn. [Requires enableList]
                            Formatting: ["minecraft:zombie", "minecraft:*", "<modId>:<entityId>"]""")
                    .translation(modid + ".config." + "MOB_LIST").defineListAllowEmpty(List.of("mobs"), () -> defaultMobList, mobs -> mobs instanceof String);

            MOB_GROUP_LIST = builder
                    .comment("""
                            Adds mobs to despawn group. Mobs in blacklisted are prevented from despawning.
                            Allowed Groups: boss, monster, villager, creature, ambient, construct, misc, blacklisted
                            Formatting: ["minecraft:villager:villager", "minecraft:*:creature", "<modId>:<entityId>:<mobCategory>"]""")
                    .translation(modid + ".config." + "MOB_GROUP_LIST").defineListAllowEmpty(List.of("groups"), () -> defaultMobGroupList, groups -> groups instanceof String);
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
                    .translation(modid + ".config." + "PLAYER_CHECK")
                    .define("playerCheck", true);

            String CATEGORY_MOB_GROUP_CHECKS = "group_checks";
            builder.comment("Group Checks [Requires enableList]")
                    .translation(modid + ".config." + "CATEGORY_MOB_GROUP_CHECKS")
                    .push(CATEGORY_MOB_GROUP_CHECKS);

            BOSS_CHECK = builder
                    .comment("Check boss group?")
                    .translation(modid + ".config." + "BOSS_CHECK")
                    .define("bossCheck", false);

            MONSTER_CHECK = builder
                    .comment("Check monster group?")
                    .translation(modid + ".config." + "MONSTER_CHECK")
                    .define("monsterCheck", true);

            VILLAGER_CHECK = builder
                    .comment("Check villager group?")
                    .translation(modid + ".config." + "VILLAGER_CHECK")
                    .define("villagerCheck", false);

            CREATURE_CHECK = builder
                    .comment("Check creature group?")
                    .translation(modid + ".config." + "CREATURE_CHECK")
                    .define("creatureCheck", true);

            AMBIENT_CHECK = builder
                    .comment("Check ambient group?")
                    .translation(modid + ".config." + "AMBIENT_CHECK")
                    .define("ambientCheck", true);

            CONSTRUCT_CHECK = builder
                    .comment("Check construct group?")
                    .translation(modid + ".config." + "CONSTRUCT_CHECK")
                    .define("constructCheck", false);

            MISC_CHECK = builder
                    .comment("Check misc group?")
                    .translation(modid + ".config." + "MISC_CHECK")
                    .define("miscCheck", false);
            builder.pop();
        }
    }

}