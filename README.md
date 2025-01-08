<div align="center"><img src="https://cdn.modrinth.com/data/cached_images/03a512a1608c6cb5e62d9d3b402024b0c932eceb.png" /></div>

<div align="center">
	<img src="https://img.shields.io/badge/mod%20loader-forge%20%2F%20neoforge-blue?style=flat-square"/>
	<img src="https://cf.way2muchnoise.eu/versions/available%20for_calm-mornings_all(555555-007ec6-fff-010101).svg?badge_style=flat">
	<a href="https://modrinth.com/mod/calm-mornings"> <img src="https://img.shields.io/modrinth/dt/gfvSVUz9?style=flat-square&logo=modrinth&logoSize=auto&color=4caf50"/></a>
	<a href="https://www.curseforge.com/minecraft/mc-mods/calm-mornings"> <img src="https://img.shields.io/curseforge/dt/683324?style=flat-square&logo=curseforge&logoSize=auto&color=4caf50"/></a>
</div>

**Calm Mornings** will look for any **Hostile** mobs(_with notible exceptions_), within a specified radius around a player upon waking up, and despawn them. This was done to help "simulate" the movement of monsters during the night.

No more creepers waiting right outside your doors, making your morning strolls a little calmer.

## Info:
This mod is highly configurable, and most aspects of what this mod does can be adjusted to suit your needs.

```toml
#General Settings
[general]
    #Use list instead of builtin rules for despawning?
    enableList = false
    #List of mobs to despawn. [Requires enableList]
    #Formatting: ["minecraft:zombie", "minecraft:*", "<modId>:<entityId>"]
    mobs = ["minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:creeper"]
    #Adds mobs to despawn group. Mobs in blacklisted are prevented from despawning.
    #Allowed Groups: boss, monster, villager, creature, ambient, construct, misc, blacklisted
    #Formatting: ["minecraft:villager:villager", "minecraft:*:creature", "<modId>:<entityId>:<group>"]
    groups = ["minecraft:ender_dragon:boss", "minecraft:wither:boss", "minecraft:warden:boss", "minecraft:villager:villager", "minecraft:wandering_trader:villager", "minecraft:iron_golem:construct", "minecraft:snow_golem:construct"]

#Range Settings
[range]
    #Should difficulty based range scaling be enabled?
    #Difficulty Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4
    enableScaling = true
    #Horizontal radius to check for mobs to despawn.
    #Range: 0 ~ 256
    horizontalRange = 64
    #Vertical radius to check for mobs to despawn.
    #Range: 0 ~ 64
    verticalRange = 16

#Conditional Checks
[checks]
    #Player must sleep before this time to allow despawning.
    #Allowed Values: MORNING_E, MORNING, MORNING_L, NOON_E, NOON, NOON_L, EVENING_E, EVENING, EVENING_L, NIGHT_E, NIGHT, NIGHT_L, DISABLED
    lateCheck = "NIGHT_L"
    #Latest time the player can wakeup to allow despawning.
    #Allowed Values: MORNING_E, MORNING, MORNING_L, NOON_E, NOON, NOON_L, EVENING_E, EVENING, EVENING_L, NIGHT_E, NIGHT, NIGHT_L, DISABLED
    morningCheck = "MORNING_E"
    #Should non-sleeping players prevent despawning around them?
    playerCheck = true

    #Group Checks [Requires enableList]
    [checks.group_checks]
        #Check boss group?
        bossCheck = false
        #Check monster group?
        monsterCheck = true
        #Check villager group?
        villagerCheck = false
        #Check creature group?
        creatureCheck = true
        #Check ambient group?
        ambientCheck = true
        #Check construct group?
        constructCheck = false
        #Check misc group?
        miscCheck = false
```
<details>
  <summary><b>FAQ:</b></summary>

**Q: How does this work with Persistent mobs?**

A: If they are named, it ignores them; otherwise it will drop their equipment when despawning them.

**Q: Where should I leave suggestions or feedback?**

A: I would love to hear any suggestions or feedback you have! I only ask that you leave it as a new issue here as it makes it easier for me to manage and track.

**Q: Can I use this in my modpack?**

A: Absolutely! I only ask that you give credit if you do. Other than that, feel free to include it and modify it however you see fit for your pack.

**Q: What version(s) will be supported?**

A: Only the latest major version will be supported. I may update older versions if there are any major bugs/oversights, but will not be backporting new features to them.

**Q: Is this compatible with X mod?**

A: I have gone out of my way to try and make this mod as compatible with as many mods as possible. If you experience any incompatibilities, please report the issue here.
</details>
