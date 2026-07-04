package salted.calmmornings.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;

public class CapProvider implements ICapabilityProvider {

    public static final ResourceLocation NAME = CalmMornings.resLoc("data");
    private final LazyOptional<ISleepTime> sleepTimeHandler;

    public CapProvider(ISleepTime sleepTimeHandler) {
        this.sleepTimeHandler = LazyOptional.of(() -> sleepTimeHandler);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == SleepTime.SLEEPTIME ? this.sleepTimeHandler.cast() : LazyOptional.empty();
    }

}