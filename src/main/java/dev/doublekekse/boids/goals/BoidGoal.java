package dev.doublekekse.boids.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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
    private final Mob mob;
    private int timeToFindNearbyEntities;
    List<? extends Mob> nearbyMobs;
    private boolean enabled = true;

    public BoidGoal(Mob mob, float separationInfluence, float separationRange, float alignmentInfluence, float cohesionInfluence) {
        timeToFindNearbyEntities = 0;

        this.mob = mob;
        this.separationInfluence = separationInfluence;
        this.separationRange = separationRange;
        this.alignmentInfluence = alignmentInfluence;
        this.cohesionInfluence = cohesionInfluence;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void tick() {
        if (!enabled) {
            return;
        }

        if (--this.timeToFindNearbyEntities <= 0) {
            this.timeToFindNearbyEntities = this.adjustedTickDelay(40);
            nearbyMobs = getNearbyEntitiesOfSameClass(mob);
        } else {
            nearbyMobs.removeIf(LivingEntity::isDeadOrDying);
        }

        if (nearbyMobs.isEmpty()) {
            LOGGER.warn("No nearby entities found. There should always be at least the entity itself. Will disable behavior for this entity instead of crash for compatibility reasons");
            enabled = false;
        }

        mob.addDeltaMovement(random());
        mob.addDeltaMovement(cohesion());
        mob.addDeltaMovement(alignment());
        mob.addDeltaMovement(separation());
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

    public Vec3 separation() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            if ((nearbyMob.position().subtract(mob.position()).length()) < separationRange) {
                c = c.subtract(nearbyMob.position().subtract(mob.position()));
            }
        }

        return c.scale(separationInfluence);
    }

    public Vec3 alignment() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            c = c.add(nearbyMob.getDeltaMovement());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.getDeltaMovement());
        return c.scale(alignmentInfluence);
    }

    public Vec3 cohesion() {
        var c = Vec3.ZERO;

        for (Mob nearbyMob : nearbyMobs) {
            c = c.add(nearbyMob.position());
        }

        c = c.scale(1f / nearbyMobs.size());
        c = c.subtract(mob.position());
        return c.scale(cohesionInfluence);
    }
}
