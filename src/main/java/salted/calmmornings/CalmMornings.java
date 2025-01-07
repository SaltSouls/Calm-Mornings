package salted.calmmornings;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import salted.calmmornings.common.Config;

import static net.neoforged.fml.config.ModConfig.Type.COMMON;
import static salted.calmmornings.common.registry.CMData.ATTACHMENTS;

@Mod(CalmMornings.MODID)
public class CalmMornings {
    public static final String MODID = "calmmornings";
    public static final Logger LOGGER = LogManager.getLogger();

    public CalmMornings(ModContainer container) {
        IEventBus modEventBus = container.getEventBus();
        container.registerConfig(COMMON, Config.COMMON_CONFIG);

        ATTACHMENTS.register(modEventBus);
    }

    public static ResourceLocation resLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
