package dev.doublekekse.boids.goals;

import dev.doublekekse.boids.BoidsSimulation;
import dev.doublekekse.boids.NearbyMobTracker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class BoidGoal extends Goal {
    private final BoidsSimulation simulation;

    private final Mob mob;
    private final NearbyMobTracker nearbyMobs;

    public BoidGoal(Mob mob, BoidsSimulation simulation) {
        this.mob = mob;
        this.simulation = simulation;
        this.nearbyMobs = new NearbyMobTracker(mob);
    }

    @Deprecated
    public BoidGoal(Mob mob, float separationInfluence, float separationRange, float alignmentInfluence, float cohesionInfluence) {
        this(mob, new BoidsSimulation(
            separationInfluence,
            separationRange,
            Float.NEGATIVE_INFINITY,
            alignmentInfluence,
            Float.NEGATIVE_INFINITY,
            cohesionInfluence,
            Float.NEGATIVE_INFINITY,
            0.04f
        ));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        mob.addDeltaMovement(simulation.apply(mob, nearbyMobs.tick()));
    }
}
