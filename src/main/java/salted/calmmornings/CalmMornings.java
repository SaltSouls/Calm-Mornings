package salted.calmmornings;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import salted.calmmornings.common.Config;

import static salted.calmmornings.common.registry.CMData.ATTACHMENTS;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CalmMornings.MODID)
public class CalmMornings {
    public static final String MODID = "calmmornings";
    public static final Logger LOGGER = LogManager.getLogger();

    public CalmMornings(ModContainer container) {
        IEventBus modEventBus = container.getEventBus();
        container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        ATTACHMENTS.register(modEventBus);
    }

    public static ResourceLocation resLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
