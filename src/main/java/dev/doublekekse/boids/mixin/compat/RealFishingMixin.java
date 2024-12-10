package dev.doublekekse.boids.mixin.compat;

import dev.doublekekse.boids.Boids;
import koala.fishingreal.FishingReal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingReal.class)
public class RealFishingMixin {
    @Inject(method = "convertItemEntity", at = @At("HEAD"))
    private static void disable(Entity fishedEntity, Player player, CallbackInfoReturnable<Entity> cir) {
        Boids.disabled = true;
    }

    @Inject(method = "convertItemEntity", at = @At("RETURN"))
    private static void enable(Entity fishedEntity, Player player, CallbackInfoReturnable<Entity> cir) {
        Boids.disabled = false;
    }
}
