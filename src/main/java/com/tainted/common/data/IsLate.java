package com.tainted.common.data;

import net.minecraft.nbt.CompoundTag;

public class IsLate {
    private boolean isLate;

    public boolean getIsLate() { return isLate; }
    public boolean setIsLate(boolean isLate) { return  this.isLate = isLate; }
    public void copyFrom(IsLate source) { isLate = source.isLate; }

    public void saveNBTData(CompoundTag compound) {
        compound.putBoolean("is late", isLate);
    }
    public void loadNBTData(CompoundTag compound) {
        compound.getBoolean("is late");
    }

}
