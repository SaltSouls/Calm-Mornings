package salted.calmmornings.common.entitylist;

import net.minecraft.world.entity.MobCategory;

public class ListInfo {
    public MobCategory category;
    public boolean despawnable;

    ListInfo(MobCategory c, boolean d) {
        category = c;
        despawnable = d;
    }

    public synchronized void setCategory(MobCategory c) {
        this.category = c;
    }

    public synchronized void setDespawnable(boolean d) {
        this.despawnable = d;
    }

    public MobCategory getCategory() { return this.category; }
    public boolean getDespawnable() { return this.despawnable; }

}
