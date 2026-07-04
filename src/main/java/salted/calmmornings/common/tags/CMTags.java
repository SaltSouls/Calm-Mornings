package salted.calmmornings.common.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import salted.calmmornings.CalmMornings;

public class CMTags {

    public static final TagKey<EntityType<?>> DEFAULT_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, CalmMornings.resLoc("default_blacklist"));

}
