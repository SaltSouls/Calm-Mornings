package salted.calmmornings.common.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SleepTime implements ISleepTime {
    private String sleepTime = "awake";

    public SleepTime() {
        super();
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
    public String getSleepTime() {
        return this.sleepTime;
    }

    @Override
    public void setSleepTime(String time) {
        this.sleepTime = time;
    }

}
