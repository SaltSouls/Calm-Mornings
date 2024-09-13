package salted.calmmornings.common;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.common.network.NetworkHandler;

public class CommonSetup {
    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            event.enqueueWork(NetworkHandler::registerMessages);
        });
    }
}
