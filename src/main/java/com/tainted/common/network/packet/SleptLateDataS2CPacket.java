package com.tainted.common.network.packet;

import com.tainted.client.data.ClientSleptLateData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SleptLateDataS2CPacket {

    private final boolean sleptLate;

    public SleptLateDataS2CPacket(boolean sleptLate) {
        this.sleptLate = sleptLate;
    }

    public SleptLateDataS2CPacket(@NotNull FriendlyByteBuf buf) {
        this.sleptLate = buf.readBoolean();
    }

    public void toBytes(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(sleptLate);
    }

    public boolean handle(@NotNull Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        //SEND PLAYER CAPABILITY TO THE CLIENT
        ctx.enqueueWork(() -> { ClientSleptLateData.set(sleptLate); });
        return true;
    }

}
