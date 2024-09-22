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
import salted.calmmornings.common.threading.ThreadManager;

import java.util.Objects;
import java.util.Set;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityListEvents {

    @SubscribeEvent
    public static void addListStartup(FMLCommonSetupEvent event) {
        Set<ResourceLocation> names = BuiltInRegistries.ENTITY_TYPE.keySet();
        ThreadManager manger = new ThreadManager();

        for (ResourceLocation resource : names) {
            Runnable task = () -> {
                String entityId = resource.toString();
                EntityType.byString(entityId).ifPresent(entity -> {
                    ListBuilder.addEntity(entityId, entity);
                    CalmMornings.LOGGER.debug("Adding [{} ", entityId + "] to map");
                });
            };
            manger.addTask(task);
        }
        manger.shutdown();
        manger.awaitShutdown(5);

        if (ModList.get().isLoaded("sleep_tight")) sleeptightCompat();
        ListBuilder.updateFilterList();

        manger = new ThreadManager();
        ListBuilder.configureEntities(Config.ENABLE_LIST.get());
        for (String mobCategory : Config.MOBCATEGORY_LIST.get()) {
            Runnable task = () -> {
                ListBuilder.updateEntityCategory(mobCategory);
            };
            manger.addTask(task);
        }
        manger.shutdown();
        manger.awaitShutdown(5);
    }

    @SubscribeEvent
    public static void configUpdated(ModConfigEvent.Reloading event) {
        if (!Objects.equals(event.getConfig().getModId(), CalmMornings.MODID)) return;
        CalmMornings.LOGGER.debug("config update event fired!");

        ListBuilder.updateFilterList();
        ListBuilder.configureEntities(Config.ENABLE_LIST.get());
        ListBuilder.configureEntities(Config.ENABLE_LIST.get());
        ThreadManager manager = new ThreadManager();

        for (String mobCategory : Config.MOBCATEGORY_LIST.get()) {
            Runnable task = () -> {
                ListBuilder.updateEntityCategory(mobCategory);
            };
            manager.addTask(task);
        }
        manager.shutdown();
        manager.awaitShutdown(5);

    }

    // private methods for determining values/conditions
    private static void sleeptightCompat() {
        ListBuilder.getBlackList().add("sleep_tight:bedbug");
    }

}
