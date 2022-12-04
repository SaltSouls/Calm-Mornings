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

public class IsLateProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {


    public static Capability<IsLate> SLEPT_LATE = CapabilityManager.get(new CapabilityToken<>(){});
    private IsLate isLate = null;
    private final LazyOptional<IsLate> opt = LazyOptional.of(this::createSleepTime);

    private IsLate createSleepTime() {
        if (isLate == null) { isLate = new IsLate(); }
        return isLate;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SLEPT_LATE) { return opt.cast(); }
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return ICapabilityProvider.super.getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createSleepTime().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { createSleepTime().loadNBTData(nbt); }

}
