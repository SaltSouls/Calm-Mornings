package salted.calmmornings.common.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.entitylist.EntityListManager;
import salted.calmmornings.common.entitylist.ListBuilder;

import java.util.HashSet;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityListEvents {


    @SubscribeEvent
    public static void addListStartup(FMLCommonSetupEvent event) {
        HashSet<String> mobList = new HashSet<>(Config.MOB_LIST.get());
        HashSet<String> categoryList = new HashSet<>(Config.MOBCATEGORY_LIST.get());

        EntityListManager.initializeMap(
                mobList,
                categoryList,
                Config.ENABLE_LIST.getAsBoolean(),
                Config.IS_BLACKLIST.getAsBoolean()
        );
    }

    @SubscribeEvent
    public static void configUpdated(ModConfigEvent.Reloading event) {
        if (!event.getConfig().getModId().equals(CalmMornings.MODID)) return;
        CalmMornings.LOGGER.debug("config update event fired!");
        HashSet<String> mobList = new HashSet<>(Config.MOB_LIST.get());
        HashSet<String> categoryList = new HashSet<>(Config.MOBCATEGORY_LIST.get());

        ListBuilder.updateFilterList();
        EntityListManager.updateMobList(mobList, Config.ENABLE_LIST.getAsBoolean(), Config.IS_BLACKLIST.getAsBoolean());
        EntityListManager.updateCategoryList(categoryList);
    }

}
