package salted.calmmornings.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.registry.CMData;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerTickEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide() && !(player instanceof ServerPlayer)) return;
        String sleepTime = player.getData(CMData.SLEEPTIME);

        if (sleepTime.equals("awake") || player.isSleeping()) return;

        player.setData(CMData.SLEEPTIME, "awake");

    }
}
