package dev.doublekekse.boids.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Predicate;

public class BoidGoal extends Goal {
    public static final Logger LOGGER = LogManager.getLogger();

    public final float separationInfluence;
    public final float separationRange;
    public final float alignmentInfluence;
    public final float cohesionInfluence;

    public final float minHeight;
    public final float maxHeight;

    public final float minSpeed;
    public final float maxSpeed;

    public Vec3 spawnPosition;
    public final float sqrRadiusFromSpawn;


    private final Mob mob;
    private int timeToRecalculatePath;
    List<? extends Mob> nearbyMobs;
    private boolean enabled = true;

    public BoidGoal(Mob mob, float separationInfluence, float separationRange, float alignmentInfluence, float cohesionInfluence, float minHeight, float maxHeight, float radiusFromSpawn, float minSpeed, float maxSpeed) {
        this.mob = mob;
        timeToRecalculatePath = 0;

        this.separationInfluence = separationInfluence;
        this.separationRange = separationRange;
        this.alignmentInfluence = alignmentInfluence;
        this.cohesionInfluence = cohesionInfluence;

        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.sqrRadiusFromSpawn = radiusFromSpawn * radiusFromSpawn;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void tick() {
        if(spawnPosition == null) {
            // This is not necessarily at the spawn, but it should be at least close to it most of the time
            spawnPosition = mob.position();
        }

        if (!enabled) {
            return;
        }

        if (--this.timeToRecalculatePath <= 0) {
            this.timeToRecalculatePath = this.adjustedTickDelay(40);
            nearbyMobs = getNearbyEntitiesOfSameClass(mob);
        }

        if (nearbyMobs.isEmpty()) {
            LOGGER.warn("No nearby entities found. There should always be at least the entity itself. Will disable behavior for this entity instead of crash for compatibility reasons");
            enabled = false;
        }

        mob.addDeltaMovement(random());
        mob.addDeltaMovement(cohesion());
        mob.addDeltaMovement(alignment());
        mob.addDeltaMovement(separation());
        mob.addDeltaMovement(bounds());

        var velocity = mob.getDeltaMovement();
        var speed = velocity.length();

        if (speed < minSpeed)
            velocity = velocity.normalize().scale(minSpeed);
        if (speed > maxSpeed)
            velocity = velocity.normalize().scale(maxSpeed);

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
        var isNegative = mob.getRandom().nextBoolean();

        if (isNegative) {
            return -1;
        }

        return 1;
    }

    public Vec3 bounds() {
        var diffX = mob.getX() - spawnPosition.x;
        var diffZ = mob.getZ() - spawnPosition.z;

        var distanceToSpawn = diffX * diffX + diffZ * diffZ;
        if (distanceToSpawn > sqrRadiusFromSpawn) {
            return new Vec3(-diffX, 0, -diffZ).scale(0.2);
        }


        var amount = 0.1;
        var dY = Mth.abs((float) mob.getDeltaMovement().y);

        if (dY > amount) {
            amount = dY;
        }

        if (mob.getY() > maxHeight) {
            return new Vec3(0, -amount, 0);
        }
        if (mob.getY() < minHeight)
            return new Vec3(0, amount, 0);

        return Vec3.ZERO;
    }

    public Vec3 separation() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if ((nearbyMob.position().subtract(mob.position()).length()) < separationRange && !nearbyMob.isDeadOrDying()) {
                c = c.subtract(nearbyMob.position().subtract(mob.position()));
            }
        }

        return c.scale(separationInfluence);
    }

    public Vec3 alignment() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if (!nearbyMob.isDeadOrDying())
                c = c.add(nearbyMob.getDeltaMovement());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.getDeltaMovement());
        return c.scale(alignmentInfluence);
    }

    public Vec3 cohesion() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if (!nearbyMob.isDeadOrDying())
                c = c.add(nearbyMob.position());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.position());
        return c.scale(cohesionInfluence);
    }
}
