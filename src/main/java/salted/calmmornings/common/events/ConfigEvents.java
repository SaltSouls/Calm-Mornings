package salted.calmmornings.common.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;


@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigEvents {

    @SubscribeEvent
    public static void onConfigUpdate(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(CalmMornings.MODID)) return;
        Config.setupDespawnLists();
    }

}