package dev.doublekekse.boids.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class HeightBoundsGoal extends Goal {
    private final Mob mob;
    private final float minHeight;
    private final float maxHeight;

    public HeightBoundsGoal(Mob mob, float minHeight, float maxHeight) {
        this.mob = mob;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public boolean canUse() {
        return mob.getY() > maxHeight || mob.getY() < maxHeight;
    }

    @Override
    public void tick() {
        mob.addDeltaMovement(bounds());
    }

    public Vec3 bounds() {
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
}
