package salted.calmmornings.common.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            SleepTime.ifPresent(player, ISleepTime::onDeath);
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(Clone event) {
        event.getOriginal().reviveCaps();

        ISleepTime oldPlayer = SleepTime.get(event.getOriginal());
        ISleepTime newPlayer = SleepTime.get(event.getEntity());
        CompoundTag tag = new CompoundTag();

        if (oldPlayer == null || newPlayer == null) return;

        if (event.isWasDeath()) {
            newPlayer.syncToClient();
            oldPlayer.write(tag);
            newPlayer.read(tag);
        }
        else {
            oldPlayer.write(tag);
            newPlayer.read(tag);
            newPlayer.syncToClient();
        }

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SleepTime.ifPresent(player, ISleepTime::syncToClient);
        }
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SleepTime.ifPresent(player, ISleepTime::syncToClient);
        }
    }
}
