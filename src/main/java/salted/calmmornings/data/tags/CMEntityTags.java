package salted.calmmornings.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.tags.CMTags;

public class CMEntityTags extends EntityTypeTagsProvider {
    public CMEntityTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, CalmMornings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.defaultBlacklist();
    }

    protected void defaultBlacklist() {
        tag(CMTags.DEFAULT_BLACKLIST)
                .add(EntityType.PLAYER, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.RAVAGER)
                .addTag(EntityTypeTags.RAIDERS)
                .addOptional(new ResourceLocation("sleep_tight", "bedbug"));
    }
}