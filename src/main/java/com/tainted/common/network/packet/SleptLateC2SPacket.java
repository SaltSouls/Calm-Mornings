package com.tainted.common.network.packet;

import com.tainted.common.config.ConfigHelper;
import com.tainted.common.data.SleptLateData;
import com.tainted.common.data.SleptLateProvider;
import com.tainted.common.utils.SleepUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SleptLateC2SPacket {

    public SleptLateC2SPacket() { }

    public SleptLateC2SPacket(@NotNull FriendlyByteBuf buf) { }

    public void toBytes(@NotNull FriendlyByteBuf buf) { }

    public boolean handle(@NotNull Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            //SET AND SEND PLAYER CAPABILITY ON THE SERVER DEPENDING ON GIVEN CONDITIONS
            ServerPlayer player = ctx.getSender();
            if (player == null) throw new AssertionError("Player should not be null!");
            ServerLevel level = player.getLevel();
            @NotNull LazyOptional<SleptLateData> capability = player.getCapability(SleptLateProvider.SLEPT_LATE);
            //SET CAPABILITY TO TRUE OR FALSE DEPENDING ON CHECK RESULTS
            if (ConfigHelper.getEnableLateCheck()) {
                capability.ifPresent(playerSleptLate -> {
                    if (!SleepUtils.isNearMorning(level)) { playerSleptLate.setNotSleptLate(); }
                    else { playerSleptLate.setSleptLate(); }
                });
            } else { capability.ifPresent(SleptLateData::setNotSleptLate); }
        });
        return true;
    }

}
