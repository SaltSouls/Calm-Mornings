package salted.calmmornings.common.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.entitylist.EntityListManager;
import salted.calmmornings.common.entitylist.ListBuilder;
import salted.calmmornings.common.threading.ThreadManager;

import java.util.HashSet;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityListEvents {

    @SubscribeEvent
    public static void addListStartup(FMLCommonSetupEvent event) {
        CalmMornings.LOGGER.debug("Constructing entity map...");
        HashSet<String> mobList = new HashSet<>(Config.MOB_LIST.get());
        HashSet<String> categoryList = new HashSet<>(Config.MOBCATEGORY_LIST.get());

        EntityListManager.initMap(
                mobList,
                categoryList,
                Config.ENABLE_LIST.getAsBoolean(),
                Config.IS_BLACKLIST.getAsBoolean()
        );
    }

    @SubscribeEvent
    public static void configUpdated(ModConfigEvent.Reloading event) {
        CalmMornings.LOGGER.debug("Currently reloading: {}.toml", event.getConfig().getModId());
        if (!event.getConfig().getModId().equals(CalmMornings.MODID)) return;
        CalmMornings.LOGGER.debug("Calm Mornings config was reloaded!");

        HashSet<String> mobList = new HashSet<>(Config.MOB_LIST.get());
        HashSet<String> categoryList = new HashSet<>(Config.MOBCATEGORY_LIST.get());
        ThreadManager manager = new ThreadManager();

        ListBuilder.updateFilterList();
        EntityListManager.updateMobList(
                mobList,
                Config.ENABLE_LIST.getAsBoolean(),
                Config.IS_BLACKLIST.getAsBoolean(),
                manager
        );
        EntityListManager.updateCategoryList(categoryList, manager);
        manager.shutdown();
        manager.awaitShutdown(5);
    }

}
