package salted.calmmornings.common.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.Config;
import salted.calmmornings.common.registry.CMData;
import salted.calmmornings.common.utils.DespawnUtils;
import salted.calmmornings.common.utils.SleepUtils;
import salted.calmmornings.common.utils.TimeUtils;
import salted.calmmornings.common.utils.TimeUtils.Time;

import java.util.List;

@EventBusSubscriber(modid = CalmMornings.MODID, bus = EventBusSubscriber.Bus.GAME)
public class SleepEvents {

    @SubscribeEvent
    public static void onPlayerSleep(CanPlayerSleepEvent event) {
        Player player = event.getEntity();
        Level level = player.level();

        // replacing the vanilla sleep check with my own
        // while I don't like this, it seems like the best method
        if (level.isClientSide()) return;
        if (level.isDay()) event.setProblem(event.getVanillaProblem());
        else if (Config.MOB_CHECK.get()) {

            if (Config.BETTER_CHECKING.get()) {
                if (noMonstersNear(level, player)) event.setProblem(null);
                else event.setProblem(event.getVanillaProblem());
            }
            else event.setProblem(event.getVanillaProblem());
        }
        else event.setProblem(null);

        // not sure if this should be done here, but this seems like the best place at this point
        Time dayTime = TimeUtils.getTimeSlice(level);
        CalmMornings.LOGGER.info("Current DayTime: {}", dayTime);
        if (dayTime == null) return; // this should never happen

        switch (dayTime) {
            case MORNING_E -> updateSleepTime("early_morning", player);
            case MORNING -> updateSleepTime("morning", player);
            case MORNING_L -> updateSleepTime("late_morning", player);
            case NOON_E -> updateSleepTime("early_afternoon", player);
            case NOON -> updateSleepTime("afternoon", player);
            case NOON_L -> updateSleepTime("late_afternoon", player);
            case EVENING_E -> updateSleepTime("early_evening", player);
            case EVENING -> updateSleepTime("evening", player);
            case EVENING_L -> updateSleepTime("late_evening", player);
            case NIGHT_E -> updateSleepTime("early_night", player);
            case NIGHT -> updateSleepTime("night", player);
            case NIGHT_L -> updateSleepTime("late_night", player);
        }
    }

    @SubscribeEvent
    public static void canContinueSleeping(CanContinueSleepingEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (!entity.isSleeping()) return;
        boolean canSleep = event.mayContinueSleeping();

        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) {
            event.setContinueSleeping(canSleep);
            return;
        }
        else if (!Config.MOB_CHECK.get() || !Config.BETTER_CHECKING.get() || !canSleep) return;
        long time = level.getGameTime();

        if (time % 10 != 0) return;
        if (noMonstersNear(level, player)) return;

        event.setContinueSleeping(false);
        player.displayClientMessage(Component.translatable(CalmMornings.MODID + ".sleep.not_safe"), true);
    }

    @SubscribeEvent
    public static void onSleepComplete(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        MinecraftServer server = level.getServer();

        if (level.isClientSide && !(player instanceof ServerPlayer)) return;
        for (ServerPlayer players : server.getPlayerList().getPlayers()) {
            // early return if player isn't sleeping/slept late
            if (SleepUtils.isPlayerValid(players)) return;

            Time levelTime = TimeUtils.getTimeSlice(level);
            Time playerTime = TimeUtils.getPlayerTimeSlice(players);
            Time timeChunk = TimeUtils.getPlayerTimeChunk(playerTime);

            switch (timeChunk) {
                case EVENING, NIGHT -> {
                    if (!SleepUtils.validWakeTime(levelTime)) return;
                    DespawnUtils.despawnEntities(level, players);
                }
            }
        }
    }

    // private methods for determining values/conditions
    private static void updateSleepTime(String time, Player player) {
        player.setData(CMData.SLEEPTIME, time);
    }

    private static boolean noMonstersNear(Level level, Player player) {
        AABB bounds = DespawnUtils.newAABB(player, 32, 16);
        List<Monster> list = level.getEntitiesOfClass(Monster.class, bounds, monster -> monster.getTarget() == player);
        return list.isEmpty();
    }

}
