package salted.calmmornings.common.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.config.IConfig;
import salted.calmmornings.common.entitylist.EntityListManager;
import salted.calmmornings.common.entitylist.ListBuilder;
import salted.calmmornings.common.threading.ThreadManager;

import java.util.HashSet;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityListEvents {
    private static boolean setupEventLoading = true; // because forge is dumb

    @SubscribeEvent
    public static void onStartup(FMLCommonSetupEvent event) {
        CalmMornings.LOGGER.debug("Constructing entity map...");
        HashSet<String> mobList = new HashSet<>(IConfig.getMobList());
        HashSet<String> categoryList = new HashSet<>(IConfig.getCategoryList());

        EntityListManager.initMap(
                mobList,
                categoryList,
                IConfig.getEnableList(),
                IConfig.isBlacklist()
        );
        setupEventLoading = false;
    }

    @SubscribeEvent
    public static void configUpdated(ModConfigEvent.Reloading event) {
        if (setupEventLoading) return;
        CalmMornings.LOGGER.debug("Currently reloading: {}.toml", event.getConfig().getModId());
        if (!event.getConfig().getModId().equals(CalmMornings.MODID)) return;
        CalmMornings.LOGGER.debug("Calm Mornings config was reloaded!");

        HashSet<String> mobList = new HashSet<>(IConfig.getMobList());
        HashSet<String> categoryList = new HashSet<>(IConfig.getCategoryList());
        ThreadManager manager = new ThreadManager();

        ListBuilder.updateFilterList();
        EntityListManager.updateMobList(
                mobList,
                IConfig.getEnableList(),
                IConfig.isBlacklist(),
                manager
        );
        EntityListManager.updateCategoryList(categoryList, manager);
        manager.shutdown();
        manager.awaitShutdown(5);
    }

}
