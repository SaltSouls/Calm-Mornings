package com.tainted.common.events;

import com.tainted.CalmMornings;
import com.tainted.common.data.SleptLateData;
import com.tainted.common.data.SleptLateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = CalmMornings.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(@NotNull AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(SleptLateProvider.SLEPT_LATE).isPresent()) {
                event.addCapability(new ResourceLocation(CalmMornings.MOD_ID, "check"), new SleptLateProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.@NotNull Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(SleptLateProvider.SLEPT_LATE).ifPresent(oldStore -> {
                event.getOriginal().getCapability(SleptLateProvider.SLEPT_LATE).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(@NotNull RegisterCapabilitiesEvent event) { event.register(SleptLateData.class); }

}
