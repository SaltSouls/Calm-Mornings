package salted.calmmornings.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import salted.calmmornings.CalmMornings;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    private static String PROTOCOL_VERSION = "S1";
    private static int index = 0;
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(CalmMornings.resLoc("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerMessages() {
        register(SyncSleepTimePacket.class, SyncSleepTimePacket::encoder, SyncSleepTimePacket::decoder, SyncSleepTimePacket::handler);
    }

    public static <MSG> void register(Class<MSG> packet, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
        INSTANCE.registerMessage(index, packet, encoder, decoder, consumer);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

}
