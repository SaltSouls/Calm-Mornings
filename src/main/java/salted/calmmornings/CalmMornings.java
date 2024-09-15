package salted.calmmornings;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import salted.calmmornings.common.CommonSetup;
import salted.calmmornings.common.config.Config;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CalmMornings.MODID)
public class CalmMornings {
    public static final String MODID = "calmmornings";
    public static final Logger LOGGER = LogManager.getLogger();

    public CalmMornings() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CommonSetup::init);

        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    public static ResourceLocation resLoc(String path) {
        return new ResourceLocation(MODID, path);
    }
}