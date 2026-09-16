package dev.doublekekse.boids.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doublekekse.boids.asm.IfMod;
import dev.doublekekse.boids.duck.MobDuck;
import koala.fishingreal.FishingReal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingReal.class)
@IfMod(value = "fishing-real", version = ">=1.10.0")
public class RealFishingMixin {
    @WrapOperation(method = "convertItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"))
    @Nullable
    private static Entity enable(EntityType<Entity> instance, Level level, EntitySpawnReason reason, Operation<Entity> original) {
        var entity = original.call(instance, level, reason);
        ((MobDuck) entity).boids$disable();
        return null;
    }
}
