package salted.calmmornings.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (!(event.phase == Phase.END)) return;
        Player player = event.player;

        if (player == null || player.isDeadOrDying()) return;
        Level level = player.level();

        if (level.isClientSide() || !(event.player instanceof ServerPlayer)) return;
        ISleepTime sleepPlayer = SleepTime.get(player);

        if (sleepPlayer == null) return;
        String sleepTime = sleepPlayer.getSleepTime();
        if (sleepTime.equals("awake") || player.isSleeping()) return;

        sleepPlayer.setSleepTime("awake");
    }

}
