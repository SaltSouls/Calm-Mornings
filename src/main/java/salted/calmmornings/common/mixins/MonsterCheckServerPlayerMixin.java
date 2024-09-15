package salted.calmmornings.common.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import salted.calmmornings.common.config.IConfig;
import salted.calmmornings.common.util.DespawnUtils;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ServerPlayer.class)
public abstract class MonsterCheckServerPlayerMixin {

    @WrapOperation(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"))
    public boolean calmMornings$monsterCheck(ServerPlayer player, Operation<Boolean> original) {
        return original.call(player) || !IConfig.getMobCheck();
    }

    @WrapOperation(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    public List betterCheck(Level instance, Class<Monster> aClass, AABB aabb, Predicate<Monster> predicate, Operation<List<Monster>> original) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        int h = IConfig.getHorizontalRange();
        int v = IConfig.getVerticalRange();
        AABB bounds = DespawnUtils.newAABB(player, h, v);

        if (IConfig.getBetterChecking()) return original.call(instance, aClass, bounds, predicate.and(monster -> monster.getTarget() == player));
        return original.call(instance, aClass, bounds, predicate);
    }
}
