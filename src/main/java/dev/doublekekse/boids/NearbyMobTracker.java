package dev.doublekekse.boids;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.function.Predicate;

public class NearbyMobTracker {
    private final Mob mob;
    private List<? extends Mob> nearbyMobs;
    private int timeToFindNearbyEntities = 0;

    public NearbyMobTracker(Mob mob) {
        this.mob = mob;
    }

    public List<? extends Mob> tick() {
        if (--this.timeToFindNearbyEntities <= 0) {
            this.timeToFindNearbyEntities = 40;
            nearbyMobs = getNearbyEntitiesOfSameClass(mob);
        } else {
            nearbyMobs.removeIf(LivingEntity::isDeadOrDying);
        }

        return nearbyMobs;
    }

    private static List<? extends Mob> getNearbyEntitiesOfSameClass(Mob mob) {
        Predicate<Mob> predicate = (_mob) -> true;

        return mob.level().getEntitiesOfClass(mob.getClass(), mob.getBoundingBox().inflate(6.0, 6.0, 6.0), predicate);
    }
}
