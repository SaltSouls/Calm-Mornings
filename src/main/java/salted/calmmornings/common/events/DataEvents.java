package salted.calmmornings.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.registry.CMData;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class DataEvents {

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.hasData(CMData.SLEEPTIME)) return;
        player.setData(CMData.SLEEPTIME, "awake");
    }

}
