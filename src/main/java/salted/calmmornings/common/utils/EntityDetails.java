package salted.calmmornings.common.utils;

import net.minecraft.world.entity.MobCategory;

public class EntityDetails {
    public MobCategory category;
    public boolean despawnable;

    EntityDetails(MobCategory c, boolean d) {
        category = c;
        despawnable = d;
    }

    public void setCategory(MobCategory c) {
        this.category = c;
    }

    public void setDespawnable(boolean d) {
        this.despawnable = d;
    }

    public MobCategory getCategory() { return this.category; }
    public boolean getDespawnable() { return this.despawnable; }


}
