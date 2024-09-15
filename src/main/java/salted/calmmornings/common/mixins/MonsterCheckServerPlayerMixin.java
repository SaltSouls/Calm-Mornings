package salted.calmmornings.common.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import salted.calmmornings.CalmMornings;
import salted.calmmornings.common.config.IConfig;
import salted.calmmornings.common.util.DespawnUtils;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class MonsterCheckServerPlayerMixin {

    @WrapOperation(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"))
    public boolean calmMornings$monsterCheck(ServerPlayer player, Operation<Boolean> original) {
        boolean shouldMixin = !IConfig.getMobCheck() || (IConfig.getMobCheck() && IConfig.getBetterChecking());

        if (player.isCreative() || !(shouldMixin)) return original.call(player);
        CalmMornings.LOGGER.debug("startSleepInBed mixin is active.");
        if (!IConfig.getMobCheck()) return true;
        else {
            CalmMornings.LOGGER.debug("betterChecking is enabled.");
            Level level = player.level();
            int h = IConfig.getHorizontalRange();
            int v = IConfig.getVerticalRange();
            AABB bounds = DespawnUtils.newAABB(player, h, v);

            List<Monster> monsters = level.getEntitiesOfClass(Monster.class, bounds, (monster) -> monster.getTarget() == player);
            return monsters.isEmpty();
        }
    }
}
