package dev.tf2levi.mischievouschest.runnable;

import dev.tf2levi.mischievouschest.Utils;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SentryRunnable extends BukkitRunnable {
    private final ArmorStand as;
    private final World playerWorld;
    private final int radius;
    int elapsedTicks;

    public SentryRunnable(ArmorStand as, World playerWorld, int radius) {
        this.as = as;
        this.playerWorld = playerWorld;
        this.radius = radius;
        elapsedTicks = 0;
    }

    @Override
    public void run() {
        if (!as.isValid()) {
            this.cancel();
            return;
        }

        // Minden 5. tickre
        if (elapsedTicks % 5 == 0) {
            Collection<Entity> nearbyEntities = playerWorld.getNearbyEntities(as.getLocation(), radius, radius, radius);
            Mob closestEntity = null;
            double closestDistance = Double.MAX_VALUE;

            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof Mob)) {
                    continue;
                }

                if (!entity.isValid()) {
                    continue;
                }

                double distance = entity.getLocation().distance(as.getLocation());
                if (distance <= radius && distance < closestDistance) {
                    closestEntity = (Mob) entity;
                    closestDistance = distance;
                }
            }

            if (closestEntity == null) {
                return;
            }

            as.setHeadPose(new EulerAngle(Utils.calculatePitch(as.getEyeLocation(), closestEntity.getEyeLocation()), 0, 0));
            as.setRotation((float) Math.toDegrees(Utils.calculateYaw(as.getEyeLocation(), closestEntity.getEyeLocation())), 0);

            Arrow bullet = (Arrow) playerWorld.spawnEntity(as.getEyeLocation(), EntityType.ARROW);

            double d0 = closestEntity.getEyeLocation().getY() - 1.100000023841858;
            double d1 = closestEntity.getLocation().getX() - as.getLocation().getX();
            double d2 = d0 - bullet.getLocation().getY();
            double d3 = closestEntity.getLocation().getZ() - as.getLocation().getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224;
            Vector velocity = new Vector(d1, d2 + d4, d3);
            velocity.normalize();
            velocity.multiply(1.6f);
            bullet.setVelocity(velocity);

            playerWorld.playSound(as.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
        }

        if (elapsedTicks == Integer.MAX_VALUE) {
            elapsedTicks = 0;
        }

        elapsedTicks++;
    }
}
