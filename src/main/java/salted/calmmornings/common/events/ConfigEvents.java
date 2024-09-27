package salted.calmmornings.common.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.config.Config;


@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEvents {

    @SubscribeEvent
    public static void onConfigUpdate(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(CalmMornings.MODID)) return;
        Config.setupDespawnLists();
    }

}