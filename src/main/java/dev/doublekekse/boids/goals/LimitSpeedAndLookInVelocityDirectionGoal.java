package dev.doublekekse.boids.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class LimitSpeedAndLookInVelocityDirectionGoal extends Goal {
    private final Mob mob;
    private final float minSpeed;
    private final float maxSpeed;

    public LimitSpeedAndLookInVelocityDirectionGoal(Mob mob, float minSpeed, float maxSpeed) {
        this.mob = mob;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        var velocity = mob.getDeltaMovement();
        var speed = velocity.length();

        // Making sure to not cause a broken state
        if (velocity.lengthSqr() < 0.01) {
            return;
        }

        if (speed < minSpeed)
            velocity = velocity.normalize().scale(minSpeed);
        if (speed > maxSpeed)
            velocity = velocity.normalize().scale(maxSpeed);

        mob.setDeltaMovement(velocity);
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, mob.getEyePosition().add(velocity.scale(3))); // Scale by 3 just to be sure it is roughly the right direction
    }
}
