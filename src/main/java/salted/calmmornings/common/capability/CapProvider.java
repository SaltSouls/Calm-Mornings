package salted.calmmornings.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;

public class CapProvider implements ICapabilitySerializable<CompoundTag> {

    public static final ResourceLocation NAME = CalmMornings.resLoc("data");
    private final LazyOptional<ISleepTime> sleepTimeHandler;

    public CapProvider(ISleepTime sleepTimeHandler) {
        this.sleepTimeHandler = LazyOptional.of(() -> sleepTimeHandler);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == SleepTime.SLEEPTIME ? this.sleepTimeHandler.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.sleepTimeHandler.orElse(null).write(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        this.sleepTimeHandler.orElse(null).read(compoundTag);
    }

}