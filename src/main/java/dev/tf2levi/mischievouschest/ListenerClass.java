package dev.tf2levi.mischievouschest;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class ListenerClass implements Listener {
    private final MischievousChest mischievousChest;

    public ListenerClass(MischievousChest mischievousChest) {
        this.mischievousChest = mischievousChest;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        Player shooter;
        if ((shooter = (Player) projectile.getShooter()) == null) {
            return;
        }

        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            arrow.setFireTicks(100);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isValid() && !arrow.isDead() && !arrow.isOnGround() && !arrow.isInBlock()) {
                        Collection<Entity> nearbyEntities = arrow.getWorld().getNearbyEntities(arrow.getLocation(), 5, 5, 5);

                        nearbyEntities.remove(shooter);

                        Entity closestEntity = null;
                        double closestDistance = Double.MAX_VALUE;

                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof LivingEntity) {
                                double distance = entity.getLocation().distance(arrow.getLocation());
                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closestEntity = entity;
                                }
                            }
                        }

                        if (closestEntity != null) {
                            Location targetLocation = closestEntity.getLocation().add(0, closestEntity.getHeight() / 2, 0);
                            Vector velocity = targetLocation.subtract(arrow.getLocation()).toVector().normalize();
                            arrow.setVelocity(velocity);
                        }
                    } else {
                        arrow.remove();
                        this.cancel();
                    }
                }
            }.runTaskTimer(mischievousChest, 0, 1);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CHEST) {
            return;
        }

        Chest chest = (Chest) block.getState();
        Location origin = block.getLocation().add(0.5, 1.5, 0.5);
        double radius = this.mischievousChest.getPluginConfig().getLaunchRadius() / 2;

        for (int i = 0; i < this.mischievousChest.getPluginConfig().getProjectileCount(); i++) {
            double x = origin.getX() + (Math.random() * radius * 2) - radius;
            double y = origin.getY() + 10;
            double z = origin.getZ() + (Math.random() * radius * 2) - radius;

            Location targetLoc = new Location(chest.getWorld(), x, y, z);
            Vector velocity = targetLoc.toVector().subtract(origin.toVector()).normalize();
            Arrow arrow = chest.getWorld().spawn(origin, Arrow.class);
            arrow.setColor(Color.fromRGB((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            arrow.setDamage(20);
            arrow.setFireTicks(20 * 20);
            arrow.setVelocity(velocity);
        }
    }
}
