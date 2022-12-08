package com.tainted.common.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SleptLateProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<SleptLateData> SLEPT_LATE = CapabilityManager.get(new CapabilityToken<SleptLateData>() { });
    private SleptLateData sleptLate = null;
    private final LazyOptional<SleptLateData> optional = LazyOptional.of(this::createPlayerSleptLate);

    private SleptLateData createPlayerSleptLate() {
        if (this.sleptLate == null) { this.sleptLate = new SleptLateData(); }
        return this.sleptLate;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SLEPT_LATE) { return optional.cast(); }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerSleptLate().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { createPlayerSleptLate().loadNBTData(nbt); }

}
