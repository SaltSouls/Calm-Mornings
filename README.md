![logo](https://cdn.modrinth.com/data/cached_images/03a512a1608c6cb5e62d9d3b402024b0c932eceb.png)

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
	#List of mobs to despawn. Requires enableList.
	#Formatting: ["minecraft:creeper", "minecraft:zombie", "minecraft:spider", "modID:entityID"]
	mobs = ["minecraft:creeper", "minecraft:zombie", "minecraft:spider"]

#Range Settings
[range]
	#Should difficulty based range scaling be enabled?
	#Difficulty Scaling: EASY = base | NORMAL = base / 2 | HARD = base / 4
	enableScaling = true
	#Vertical radius to check for mobs to despawn.
	#Range: 0 ~ 64
	verticalRange = 16
	#Horizontal radius to check for mobs to despawn.
	#Range: 0 ~ 256
	horizontalRange = 64

#Conditional Checks
[checks]
	#Latest time a player can sleep to allow despawning.
	#Allowed Values: MORNING_E, MORNING, MORNING_L, NOON_E, NOON, NOON_L, EVENING_E, EVENING, EVENING_L, NIGHT_E, NIGHT, NIGHT_L, DISABLED
	lateCheck = "NIGHT_L"
	#Should non-sleeping players prevent despawning around them?
	playerCheck = true
	#Should nearby monsters prevent sleep?
	monsterCheck = true
	#Should only monsters tracking the player prevent sleep? Requires monsterCheck.
	betterChecking = true
```
## FAQ:
**Q: Where should I leave suggestions or feedback?**

A: I would love to hear any suggestions or feedback you have! I only ask that you leave it as a new issue over on the mods Github as it makes it easier for me to manage and track.

**Q: Can I use this in my modpack?**

A: Absolutely! I only ask that you give credit if you do. Other than that, feel free to include it and modify it however you see fit for your pack.

**Q: What version will be supported?**

A: Only the latest major versions will be supported. I may update older versions if there are any major bugs/oversights, but will not be backporting new features to them.

**Q: Is this compatible with X mod?**

A: I have gone out of my way to try and make this mod as compatible with as many mods as possible. If you experience any incompatibilities, please report the issue on the mods Github.

**Q: Fabric port?**

A: I have **No** plans on porting to Fabric, but you are more than welcome to do so. I only ask for credit if you do.
