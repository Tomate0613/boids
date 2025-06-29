package dev.doublekekse.boids.mixin;

import dev.doublekekse.boids.Boids;
import dev.doublekekse.boids.NearbyMobTracker;
import dev.doublekekse.boids.duck.MobDuck;
import dev.doublekekse.boids.goals.LimitSpeedAndLookInVelocityDirectionGoal;
import dev.doublekekse.boids.goals.StayInWaterGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements MobDuck {
    @Unique
    NearbyMobTracker boids$nearbyMobs;
    @Unique
    Goal boids$stayInWaterGoal;
    @Unique
    Goal boids$limitSpeedGoal;

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);

    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        if (!Boids.isAffected(this)) {
            return;
        }

        boids$enable();
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    void serverAiStep(CallbackInfo ci) {
        if (boids$nearbyMobs == null) { // Not affected
            return;
        }

        ci.cancel();

        addDeltaMovement(Boids.SETTINGS.apply((Mob) (Object) this, boids$nearbyMobs.tick()));

        if (boids$stayInWaterGoal != null) {
            boids$stayInWaterGoal.tick();
        }

        boids$limitSpeedGoal.tick();
    }

    @Override
    public void boids$enable() {
        if (getMobType() == MobType.WATER) {
            boids$stayInWaterGoal = new StayInWaterGoal((Mob) (Object) this);
        }

        boids$limitSpeedGoal = new LimitSpeedAndLookInVelocityDirectionGoal((Mob) (Object) this, Boids.CONFIG.minSpeed, Boids.CONFIG.maxSpeed);
        boids$nearbyMobs = new NearbyMobTracker((Mob) (Object) this);
    }

    @Override
    public void boids$disable() {
        boids$stayInWaterGoal = null;
        boids$limitSpeedGoal = null;
        boids$nearbyMobs = null;
    }
}
