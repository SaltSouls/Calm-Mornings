package salted.calmmornings.common.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;

import java.util.Objects;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ConfigUpdate {
    @SubscribeEvent
    public static void reloadEvent(ModConfigEvent.Reloading e) {
        if (Objects.equals(e.getConfig().getModId(), CalmMornings.MODID)) {
            Config.reloadHashMap();
        }
    }
}
