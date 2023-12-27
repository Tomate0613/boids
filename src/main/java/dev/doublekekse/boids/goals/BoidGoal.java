package dev.doublekekse.boids.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class BoidGoal extends Goal {
    public final float SEPARATION_INFLUENCE;
    public final float SEPARATION_RANGE;
    public final float ALIGNMENT_INFLUENCE;
    public final float COHESION_INFLUENCE;

    public final float MIN_HEIGHT;
    public final float MAX_HEIGHT;

    public final float MIN_SPEED;
    public final float MAX_SPEED;


    private final Mob mob;
    private int timeToRecalculatePath;
    List<? extends Mob> nearbyMobs;

    public BoidGoal(Mob mob, float separationInfluence, float separationRange, float alignmentInfluence, float cohesionInfluence, float minHeight, float maxHeight, float minSpeed, float maxSpeed) {
        this.mob = mob;
        timeToRecalculatePath = 0;

        SEPARATION_INFLUENCE = separationInfluence;
        SEPARATION_RANGE = separationRange;
        ALIGNMENT_INFLUENCE = alignmentInfluence;
        COHESION_INFLUENCE = cohesionInfluence;

        MIN_HEIGHT = minHeight;
        MAX_HEIGHT = maxHeight;

        MIN_SPEED = minSpeed;
        MAX_SPEED = maxSpeed;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void tick() {
        if (--this.timeToRecalculatePath <= 0) {
            this.timeToRecalculatePath = this.adjustedTickDelay(40);
            nearbyMobs = getNearbyEntitiesOfSameClass(mob);
        }

        if (nearbyMobs.isEmpty()) {
            throw new Error("No nearby entities found. There should always be at least the entity itself");
        }

        mob.addDeltaMovement(random());
        mob.addDeltaMovement(cohesion());
        mob.addDeltaMovement(alignment());
        mob.addDeltaMovement(separation());
        mob.addDeltaMovement(bounds());

        var velocity = mob.getDeltaMovement();
        var speed = velocity.length();

        if (speed < MIN_SPEED)
            velocity = velocity.normalize().scale(MIN_SPEED);
        if (speed > MAX_SPEED)
            velocity = velocity.normalize().scale(MAX_SPEED);

        mob.setDeltaMovement(velocity);
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, mob.position().add(velocity.scale(3)));
    }

    public static List<? extends Mob> getNearbyEntitiesOfSameClass(Mob mob) {
        Predicate<Mob> predicate = (_mob) -> true;

        return mob.level().getEntitiesOfClass(mob.getClass(), mob.getBoundingBox().inflate(4.0, 4.0, 4.0), predicate);
    }

    public Vec3 random() {
        var velocity = mob.getDeltaMovement();

        if (Mth.abs((float) velocity.x) < 0.1 && Mth.abs((float) velocity.z) < 0.1)
            return new Vec3(randomSign() * 0.2, 0, randomSign() * 0.2);

        return Vec3.ZERO;
    }

    public int randomSign() {
        var u = mob.getRandom().nextBoolean();

        if(u) {
            return -1;
        }

        return 1;
    }

    public Vec3 bounds() {
        var amount = 0.1;
        var dY = Mth.abs((float) mob.getDeltaMovement().y);

        if (dY > amount) {
            amount = dY;
        }

        if (mob.getY() > MAX_HEIGHT) {
            return new Vec3(0, -amount, 0);
        }
        if (mob.getY() < MIN_HEIGHT)
            return new Vec3(0, amount, 0);

        return Vec3.ZERO;
    }

    public Vec3 separation() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if ((nearbyMob.position().subtract(mob.position()).length()) < SEPARATION_RANGE && !nearbyMob.isDeadOrDying()) {
                c = c.subtract(nearbyMob.position().subtract(mob.position()));
            }
        }

        return c.scale(SEPARATION_INFLUENCE);
    }

    public Vec3 alignment() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if (!nearbyMob.isDeadOrDying())
                c = c.add(nearbyMob.getDeltaMovement());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.getDeltaMovement());
        return c.scale(ALIGNMENT_INFLUENCE);
    }

    public Vec3 cohesion() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if (!nearbyMob.isDeadOrDying())
                c = c.add(nearbyMob.position());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.position());
        return c.scale(COHESION_INFLUENCE);
    }
}
