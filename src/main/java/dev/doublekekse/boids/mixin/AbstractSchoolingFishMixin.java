package dev.doublekekse.boids.mixin;

import dev.doublekekse.boids.Boids;
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

import static dev.doublekekse.boids.Boids.CONFIG;

@Mixin(AbstractSchoolingFish.class)
public abstract class AbstractSchoolingFishMixin extends AbstractFish {
    public AbstractSchoolingFishMixin(EntityType<? extends AbstractFish> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    protected void registerGoals(CallbackInfo ci) {
        var type = this.getType().toString();

        if (CONFIG.excludeEntities.contains(type)) {
            return;
        }

        if (Boids.disabled) {
            return;
        }

        this.goalSelector.addGoal(5, new BoidGoal(this, CONFIG.separationInfluence, CONFIG.separationRange, CONFIG.alignmentInfluence, CONFIG.cohesionInfluence));
        this.goalSelector.addGoal(3, new StayInWaterGoal(this));
        this.goalSelector.addGoal(2, new LimitSpeedAndLookInVelocityDirectionGoal(this, CONFIG.minSpeed, CONFIG.maxSpeed));

        ci.cancel();
    }
}
