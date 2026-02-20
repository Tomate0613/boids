package dev.doublekekse.boids;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.List;
import java.util.function.Predicate;

public class NearbyMobTracker {
    private final Mob mob;
    private List<Mob> nearbyMobs;
    private int timeToFindNearbyEntities = 0;

    public NearbyMobTracker(Mob mob) {
        this.mob = mob;
    }

    public List<? extends Entity> tick() {
        if (--this.timeToFindNearbyEntities <= 0) {
            this.timeToFindNearbyEntities = 40;
            nearbyMobs = getNearbyEntitiesOfSameType(mob);
        } else {
            nearbyMobs.removeIf(LivingEntity::isDeadOrDying);
        }

        return nearbyMobs;
    }

    private static List<Mob> getNearbyEntitiesOfSameType(Mob mob) {
        Predicate<Mob> predicate = (other) -> other.getType() == mob.getType();
        var boundingBox = mob.getBoundingBox().inflate(6.0, 6.0, 6.0);

        return mob.level().getEntities(EntityTypeTest.forClass(Mob.class), boundingBox, predicate);
    }
}
