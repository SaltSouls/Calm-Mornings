package salted.calmmornings.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import salted.calmmornings.CalmMornings;

@Mod(value = CalmMornings.MODID, dist = Dist.CLIENT)
public class CalmMorningsClient {
    public CalmMorningsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
