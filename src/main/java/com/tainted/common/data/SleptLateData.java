package com.tainted.common.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class SleptLateData {

    private boolean sleptLate;

    public boolean getSleptLate() { return sleptLate; }

    public void setSleptLate() { sleptLate = true; }

    public void setNotSleptLate() { sleptLate = false; }

    public void copyFrom(@NotNull SleptLateData source) { this.sleptLate = source.sleptLate; }

    public void saveNBTData(@NotNull CompoundTag nbt) { nbt.putBoolean("sleptLate", sleptLate); }

    public void loadNBTData(@NotNull CompoundTag nbt) { nbt.getBoolean("sleptLate"); }

}
