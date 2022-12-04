package com.tainted;

import com.tainted.common.Config;
import com.tainted.common.events.IsLateEvents;
import com.tainted.common.events.PlayerSleepEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("calmmornings")
public class CalmMornings {
    public static final String MOD_ID = "calmmornings";

    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
//        bus.addGenericListener(Entity.class, IsLateEvents::onAttachCapabilitiesPlayer);
//        bus.addListener(IsLateEvents::onPlayerCloned);
//        bus.addListener(IsLateEvents::onRegisterCapabilities);
//        bus.addListener(PlayerSleepEvents::onPlayerWakeUp);
    }
    public CalmMornings() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.COMMON_SPEC);
    }

    @SubscribeEvent
    public static void preInit(final FMLCommonSetupEvent event) {

    }

}
