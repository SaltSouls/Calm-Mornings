package salted.calmmornings.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@AutoRegisterCapability
public interface ISleepTime {

    Capability<ISleepTime> SLEEPTIME = CapabilityManager.get(new CapabilityToken<>() {
    });

    CompoundTag write(CompoundTag tag);

    void read(CompoundTag tag);

    String getSleepTime();

    void setSleepTime(String time);

}
