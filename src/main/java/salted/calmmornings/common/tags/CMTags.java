package salted.calmmornings.common.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import salted.calmmornings.CalmMornings;

public class CMTags {

    private static <T> ResourceKey<Registry<T>> createRegistryKey() {
        return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("entity_type"));
    }

    public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE = createRegistryKey();

    public static final TagKey<EntityType<?>> DEFAULT_BLACKLIST = TagKey.create(
            ENTITY_TYPE,
            CalmMornings.resLoc("default_blacklist")
    );

}