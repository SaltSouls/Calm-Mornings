package com.tainted.common.network;

import com.tainted.CalmMornings;
import com.tainted.common.network.packet.SleptLateDataS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        String modid = CalmMornings.MOD_ID;
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modid, "packets"))
                .networkProtocolVersion(() -> "CM1")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(SleptLateDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SleptLateDataS2CPacket::new)
                .encoder(SleptLateDataS2CPacket::toBytes)
                .consumerMainThread(SleptLateDataS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
