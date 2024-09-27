package salted.calmmornings.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.data.tags.CMEntityTags;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CMEntityTags entityTags = new CMEntityTags(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), entityTags);
    }

}