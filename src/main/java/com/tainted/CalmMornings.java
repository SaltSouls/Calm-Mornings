package com.tainted;

import com.tainted.common.config.Config;
import com.tainted.common.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

@Mod("calmmornings")
public class CalmMornings {
    public static final String MOD_ID = "calmmornings";

    public CalmMornings() {

        CalmMornings.setup();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(CalmMornings::commonSetup);
        bus.register(this);

    }

    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
    }

    public static void commonSetup(final @NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }

}
