package dev.tf2levi.mischievouschest.runnable;

import dev.tf2levi.mischievouschest.MischievousChest;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Collection;

public class HomingProjectile extends BukkitRunnable {
    private final MischievousChest mischievousChest;
    private final Arrow arrow;
    private final Player shooter;
    private BukkitTask task;

    public HomingProjectile(MischievousChest mischievousChest, Arrow arrow, Player shooter) {
        this.mischievousChest = mischievousChest;
        this.arrow = arrow;
        this.shooter = shooter;
        this.task = null;
    }

    public void startTracking() {
        task = this.runTaskTimer(mischievousChest, 0, 1);
    }

    public void stopTracking() {
        task.cancel();
    }

    public BukkitTask getTrackerTask() {
        return this.task;
    }

    @Override
    public void run() {
        if (arrow.isValid() && !arrow.isDead() && !arrow.isOnGround() && !arrow.isInBlock()) {
            Collection<Entity> nearbyEntities = arrow.getWorld().getNearbyEntities(arrow.getLocation(), 5, 5, 5);

            nearbyEntities.remove(shooter);

            Mob closestMob = null;
            double closestDistance = Double.MAX_VALUE;

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Mob) {
                    double distance = entity.getLocation().distance(arrow.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestMob = (Mob) entity;
                    }
                }
            }

            if (closestMob != null) {
                Location targetLocation = closestMob.getLocation().add(0, closestMob.getHeight() / 2, 0);
                Vector velocity = targetLocation.subtract(arrow.getLocation()).toVector().normalize();
                arrow.setVelocity(velocity);
            }
        } else {
            arrow.remove();
            this.cancel();
        }
    }
}