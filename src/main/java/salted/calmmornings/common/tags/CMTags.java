package salted.calmmornings.common.tags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import salted.calmmornings.CalmMornings;

public class CMTags {

    public static final TagKey<EntityType<?>> DEFAULT_BLACKLIST = TagKey.create(
            BuiltInRegistries.ENTITY_TYPE.key(),
            CalmMornings.resLoc("default_blacklist")
    );

}
