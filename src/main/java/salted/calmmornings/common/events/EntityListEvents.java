package salted.calmmornings.common.events;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.entitylist.ListBuilder;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityListEvents {

    @SubscribeEvent
    public static void onStartup(FMLCommonSetupEvent event) {
        Set<ResourceLocation> names = BuiltInRegistries.ENTITY_TYPE.keySet();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        for (ResourceLocation loc : names) {
            Runnable runnable = () -> {
                String entity_id = loc.toString();
                EntityType.byString(entity_id).ifPresent(entity -> {
                    CalmMornings.LOGGER.debug("Adding entity " + entity_id + " to map");
                });
            };
            pool.execute(runnable);
        }
        pool.shutdown();
        try {
            boolean didShutDown = pool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            CalmMornings.LOGGER.debug("Failed to shutdown threadpool in a timely manner");
        }
        if (ModList.get().isLoaded("sleep_tight")) sleeptightCompat();
        ListBuilder.hydrateEntities(!Config.ENABLE_LIST.get());
    }

    @SubscribeEvent
    public static void configUpdated(ModConfigEvent.Reloading event) {
        if (!Objects.equals(event.getConfig().getModId(), CalmMornings.MODID)) return;

        ListBuilder.hydrateEntities(!Config.ENABLE_LIST.get());
        CalmMornings.LOGGER.debug("config update event fired!");
    }

    // private methods for determining values/conditions
    private static void sleeptightCompat() {
        ListBuilder.getBlackList().add("sleep_tight:bedbug");
    }

}
