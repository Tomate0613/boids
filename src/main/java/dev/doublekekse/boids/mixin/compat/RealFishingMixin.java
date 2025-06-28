package dev.doublekekse.boids.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doublekekse.boids.Boids;
import dev.doublekekse.boids.duck.MobDuck;
import koala.fishingreal.FishingReal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingReal.class)
public class RealFishingMixin {
    @Inject(method = "convertItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;"))
    private static void enable(ItemStack itemstack, Player player, Vec3 position, CallbackInfoReturnable<Entity> cir, @Local Mob mob) {
        ((MobDuck) mob).boids$disable();
    }
}
