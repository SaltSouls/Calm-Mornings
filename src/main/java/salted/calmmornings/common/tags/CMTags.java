package salted.calmmornings.common.tags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import salted.calmmornings.CalmMornings;

public class CMTags {

    public static final TagKey<EntityType<?>> DEFAULT_BLACKLIST = TagKey.create(
            ForgeRegistries.ENTITIES.getRegistryKey(),
            CalmMornings.resLoc("default_blacklist")
    );

}