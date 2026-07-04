package salted.calmmornings.common.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@AutoRegisterCapability
public interface ISleepTime {

    Capability<ISleepTime> SLEEPTIME = CapabilityManager.get(new CapabilityToken<>() {
    });

    String getSleepTime();

    void setSleepTime(String time);

}
