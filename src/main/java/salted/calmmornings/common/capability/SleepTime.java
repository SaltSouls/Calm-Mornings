package salted.calmmornings.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import salted.calmmornings.common.network.NetworkHandler;
import salted.calmmornings.common.network.SyncSleepTimePacket;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SleepTime implements ISleepTime {
    private Player player;
    private String sleepTime = "";


    public SleepTime(Player player) {
        super();
        this.player = player;
    }

    public static ISleepTime get(Player player) {
        return getIfPreset(player, sleepPlayer -> sleepPlayer, () -> null);
    }


    public static <E extends Player> void ifPresent(E player, Consumer<ISleepTime> consumer) {
        if (player == null || player instanceof FakePlayer) return;

        Optional<ISleepTime> optional = player.getCapability(ISleepTime.SLEEPTIME).resolve();
        optional.ifPresent(consumer);
    }

    public static <E extends Player, R> R getIfPreset(E player, Function<ISleepTime, R> action, Supplier<R> elseSupplier) {
        if (player == null || player instanceof FakePlayer) return elseSupplier.get();

        Optional<ISleepTime> optional = player.getCapability(ISleepTime.SLEEPTIME).resolve();
        return optional.map(action).orElse(elseSupplier.get());
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        tag.putString("sleeptime", this.getSleepTime());
        return tag;
    }

    @Override
    public void read(CompoundTag tag) { this.setSleepTime(tag.getString("sleeptime")); }

    @Override
    public String getSleepTime() { return this.sleepTime; }

    @Override
    public void setSleepTime(String time) { this.sleepTime = time; }

    @Override
    public Player getPlayer() { return this.player; }

    @Override
    public void onDeath() { }

    @Override
    public void syncToClient() {
        if (getPlayer() instanceof ServerPlayer player) {
            NetworkHandler.sendToClient(new SyncSleepTimePacket(this.write(new CompoundTag())), player);
        }
    }
}
