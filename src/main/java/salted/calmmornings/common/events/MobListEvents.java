package salted.calmmornings.common.events;

import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.utils.DespawnUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.common.utils.MobListUtils;

import java.util.Objects;
import java.util.Set;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MobListEvents {

    @SubscribeEvent
    private static void onStartup(FMLCommonSetupEvent event) {
        Marker marker = MarkerManager.getMarker("[CalmMornings]");
        Set<ResourceLocation> names = BuiltInRegistries.ENTITY_TYPE.keySet();
        for (ResourceLocation loc : names) {
            String entity_id = loc.toString();
            EntityType.byString(entity_id).ifPresent(entity -> {
                CalmMornings.LOGGER.debug(marker, "Adding entity " + entity_id + " to map");
                MobListUtils.addEntity(entity_id, entity);
            });
        }
    }

    @SubscribeEvent
    private static void configUpdated(ModConfigEvent.Reloading event) {
        if (Objects.equals(event.getConfig().getModId(), CalmMornings.MODID)) {
            MobListUtils.hydrateEntities();
            CalmMornings.LOGGER.debug("config update event fired!");
        }
    }
}
