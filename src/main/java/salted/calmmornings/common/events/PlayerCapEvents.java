package salted.calmmornings.common.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.capability.CapProvider;
import salted.calmmornings.common.capability.ISleepTime;
import salted.calmmornings.common.capability.SleepTime;

@Mod.EventBusSubscriber(modid = CalmMornings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerCapEvents {

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(CapProvider.NAME, new CapProvider(new SleepTime(player)));
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SleepTime.ifPresent(player, sleepTime -> sleepTime.setSleepTime("awake"));
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(@NotNull RegisterCapabilitiesEvent event) { event.register(ISleepTime.class); }

}
