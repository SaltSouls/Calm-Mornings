package salted.calmmornings.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import salted.calmmornings.common.capability.SleepTime;

import java.util.function.Supplier;

public record SyncSleepTimePacket(CompoundTag nbt) {

    public static void encoder(SyncSleepTimePacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.nbt);
    }

    public static SyncSleepTimePacket decoder(FriendlyByteBuf buf) {
        return new SyncSleepTimePacket(buf.readNbt());
    }

    public static void handler(SyncSleepTimePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(packet)));
        ctx.get().setPacketHandled(true);
    }

    private static void handleClient(SyncSleepTimePacket packet) {
        Player player = net.minecraft.client.Minecraft.getInstance().player;
        SleepTime.ifPresent(player, sleepPlayer -> sleepPlayer.read(packet.nbt));
    }
}
