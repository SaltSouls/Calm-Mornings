package com.tainted.common.events;

import com.tainted.CalmMornings;
import com.tainted.common.data.IsLate;
import com.tainted.common.data.IsLateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IsLateEvents {

    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(IsLateProvider.SLEPT_LATE).isPresent()) {
                event.addCapability(new ResourceLocation(CalmMornings.MOD_ID, "is late"), new IsLateProvider());
            }
        }
    }

    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(IsLateProvider.SLEPT_LATE).ifPresent(oldStore -> {
                event.getEntity().getCapability(IsLateProvider.SLEPT_LATE).ifPresent(newStore -> { newStore.copyFrom(oldStore); });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IsLate.class);
    }

    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) {

        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

    }

}