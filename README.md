<div align="center"><img src="https://cdn.modrinth.com/data/cached_images/03a512a1608c6cb5e62d9d3b402024b0c932eceb.png" /></div>

<div align="center">
	<img src="https://img.shields.io/badge/mod%20loader-forge%20%2F%20neoforge-blue?style=flat-square" />
	<img src="https://img.shields.io/modrinth/game-versions/gfvSVUz9?style=flat-square&label=avaliable%20for&color=blue" />
	<img src="https://img.shields.io/modrinth/dt/gfvSVUz9?style=flat-square&logo=modrinth&logoSize=auto&color=4caf50" />
	<img src="https://img.shields.io/curseforge/dt/683324?style=flat-square&logo=curseforge&logoSize=auto&color=4caf50" />
</div>

**Calm Mornings** will look for any **Hostile** mobs(_with notible exceptions_), within a specified radius around a player upon waking up, and despawn them. This was done to help "simulate" the movement of monsters during the night.

No more creepers waiting right outside your doors, making your morning strolls a little calmer.

## Info:
This mod is highly configurable, and most aspects of what this mod does can be adjusted to suit your needs.

```toml
#General Settings
[general]
	#Use list instead of mobCategory for despawning?
	enableList = false
	#Changes the list to be a blacklist. Requires enableList.
	isBlacklist = false
	#List of mobs to despawn. '*' adds all entities in modId. Requires enableList.
	#Formatting: ["minecraft:zombie", "minecraft:skeleton", "<modId>:<entityId>"]
	mobs = ["minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:creeper"]
	#Change mob's viewed MobCategory when despawning. '*' adds all entities in modId.
	#Formatting: ["minecraft:villager:creature", "<modId>:<entityId>:<mobCategory>"]
	#Allowed Categories: [monster, creature, water_creature, underground_water_creature, ambient, water_ambient, misc]
	changed = ["minecraft:villager:creature", "minecraft:iron_golem:creature", "minecraft:snow_golem:creature"]

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
	#Should nearby monsters prevent sleep?
	monsterCheck = true
	#Should only monsters tracking the player prevent sleep? Requires monsterCheck.
	betterChecking = true

	#Allow listed MobCategories when despawning? Requires enableList.
	[checks.category_checks]
		#Enable MONSTER check?
		MONSTER = true
		#Enable CREATURE check?
		CREATURE = true
		#Enable AXOLOTLS check?
		AXOLOTLS = true
		#Enable WATER_CREATURE check?
		WATER_CREATURE = true
		#Enable UNDERGROUND_WATER_CREATURE check?
		UNDERGROUND_WATER_CREATURE = true
		#Enable AMBIENT check?
		AMBIENT = true
		#Enable WATER_AMBIENT check?
		WATER_AMBIENT = true
		#Enable MISC check?
		MISC = false
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

**Q: Fabric port?**

A: I have **No** plans on porting to Fabric, but you are more than welcome to do so. I only ask for credit if you do.
</details>
