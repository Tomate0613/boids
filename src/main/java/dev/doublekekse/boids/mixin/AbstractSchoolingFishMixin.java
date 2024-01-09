package dev.doublekekse.boids.mixin;

import dev.doublekekse.boids.goals.BoidGoal;
import dev.doublekekse.boids.goals.LimitSpeedAndLookInVelocityDirectionGoal;
import dev.doublekekse.boids.goals.StayInWaterGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSchoolingFish.class)
public abstract class AbstractSchoolingFishMixin extends AbstractFish {
    public AbstractSchoolingFishMixin(EntityType<? extends AbstractFish> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    protected void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(5, new BoidGoal(this, 0.5f, 0.9f, 8 / 20f, 1 / 20f));
        this.goalSelector.addGoal(3, new StayInWaterGoal(this));
        this.goalSelector.addGoal(2, new LimitSpeedAndLookInVelocityDirectionGoal(this, 0.3f, 0.8f));

        ci.cancel();
    }
}
