package salted.calmmornings.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.managers.utils.TimeUtils.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Config {
    public static ModConfigSpec COMMON_CONFIG;

    public static void setupDespawnLists() {
        // clear entries
        MOB_GROUP_MAP.clear();
        MOB_SET.clear();

        // build maps
        if (!MOB_GROUP_LIST.get().isEmpty()) {
            MOB_GROUP_LIST.get().forEach(mob -> {
                String[] split = mob.split(":");
                if (split.length < 3) {
                    CalmMornings.LOGGER.error("[{}] does not contain a valid mob category!", mob);
                    return;
                }
                MOB_GROUP_MAP.put(split[0] + ":" + split[1], split[2]);
            });
        }

        if (MOB_LIST.get().isEmpty()) return;
        MOB_SET.addAll(MOB_LIST.get());
    }

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
    public static final HashSet<String> MOB_SET = new HashSet<>();
    public static final HashMap<String, String> MOB_GROUP_MAP = new HashMap<>();

    public static ModConfigSpec.BooleanValue ENABLE_LIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_LIST;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_GROUP_LIST;
    public static ModConfigSpec.BooleanValue ENABLE_SCALING;
    public static ModConfigSpec.IntValue VERTICAL_RANGE;
    public static ModConfigSpec.IntValue HORIZONTAL_RANGE;
    public static ModConfigSpec.EnumValue<Time> LATE_CHECK;
    public static ModConfigSpec.EnumValue<Time> MORNING_CHECK;
    public static ModConfigSpec.BooleanValue PLAYER_CHECK;
    public static ModConfigSpec.BooleanValue BOSS_CHECK;
    public static ModConfigSpec.BooleanValue MONSTER_CHECK;
    public static ModConfigSpec.BooleanValue VILLAGER_CHECK;
    public static ModConfigSpec.BooleanValue CREATURE_CHECK;
    public static ModConfigSpec.BooleanValue AMBIENT_CHECK;
    public static ModConfigSpec.BooleanValue CONSTRUCT_CHECK;
    public static ModConfigSpec.BooleanValue MISC_CHECK;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        String modid = CalmMornings.MODID;
        String CATEGORY_GENERAL = "general";
        builder.comment("General Settings").translation(modid + ".config." + "CATEGORY_GENERAL").push(CATEGORY_GENERAL);
        ENABLE_LIST = builder
                .comment("Use list instead of built-in rules for despawning?")
                .translation(modid + ".config." + "ENABLE_LIST")
                .define("enableList", false);

        MOB_LIST = builder
                .comment("""
                            List of mobs to despawn. [Requires enableList]
                            Formatting: ["minecraft:zombie", "minecraft:*", "<modId>:<entityId>"]""")
                .translation(modid + ".config." + "MOB_LIST").defineListAllowEmpty(List.of("mobs"), () -> defaultMobList, () -> "", mobs -> mobs instanceof String);

        MOB_GROUP_LIST = builder
                .comment("""
                            Adds mobs to despawn group. Mobs in blacklisted are prevented from despawning.
                            Allowed Groups: boss, monster, villager, creature, ambient, construct, misc, blacklisted
                            Formatting: ["minecraft:villager:villager", "minecraft:*:creature", "<modId>:<entityId>:<group>"]""")
                .translation(modid + ".config." + "MOB_GROUP_LIST").defineListAllowEmpty(List.of("groups"), () -> defaultMobGroupList, () -> "", groups -> groups instanceof String);
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

        COMMON_CONFIG = builder.build();
    }

}