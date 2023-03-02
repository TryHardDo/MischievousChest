package dev.tf2levi.mischievouschest;

import dev.tf2levi.mischievouschest.runnable.HomingProjectile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListenerClass implements Listener {
    private final MischievousChest mischievousChest;
    private final List<UUID> sentryIds = new ArrayList<>();

    public ListenerClass(MischievousChest mischievousChest) {
        this.mischievousChest = mischievousChest;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (source == null) {
            return;
        }

        if (!(source instanceof Player)) {
            return;
        }

        Player shooter = ((Player) source);

        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            arrow.setFireTicks(100);
            new HomingProjectile(mischievousChest, arrow, shooter).startTracking();
        }
    }

    @EventHandler
    public void onSentryPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack interactItem = e.getItem();
        if (interactItem == null || e.getClickedBlock() == null) {
            return;
        }

        if (interactItem.getType() != Material.STICK) {
            return;
        }

        Block clickedBlock = e.getClickedBlock();

        if (!clickedBlock.getType().isSolid()) {
            return;
        }

        Player placer = e.getPlayer();
        World playerWorld = placer.getWorld();
        Location clickedLocation = clickedBlock.getRelative(BlockFace.UP).getLocation().subtract(0, 0.3, 0);

        ArmorStand as = (ArmorStand) playerWorld.spawnEntity(clickedLocation, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setSmall(false);
        as.setGravity(false);
        as.setHealth(20);
        as.setBasePlate(false);
        as.setArms(false);
        as.setCustomName("Sentry");
        as.setCustomNameVisible(true);

        EntityEquipment equipment = as.getEquipment();
        assert equipment != null;

        equipment.setHelmet(new ItemStack(Material.DISPENSER), true);

        int radius = 10;
        new BukkitRunnable() {
            int elapsedTicks = 0;

            @Override
            public void run() {
                if (!as.isValid()) {
                    this.cancel();
                }

                // Minden 5. tickre
                if (elapsedTicks % 5 == 0) {
                    List<Entity> nearbyEntities = playerWorld.getEntities();

                    Mob closestEntity = null;
                    double closestDistance = Double.MAX_VALUE;

                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Mob) {
                            double distance = entity.getLocation().distance(as.getLocation());
                            if (distance <= radius && distance < closestDistance) {
                                closestEntity = (Mob) entity;
                                closestDistance = distance;
                            }
                        }
                    }

                    if (closestEntity == null) {
                        return;
                    }

                    Vector diff = closestEntity.getLocation().subtract(as.getLocation()).toVector().normalize();

                    double angleY = Math.atan2(-diff.getX(), diff.getZ());
                    double angleXZ = Math.sqrt(Math.pow(diff.getX(), 2) + Math.pow(diff.getZ(), 2));
                    double angleX = Math.atan2(diff.getY(), angleXZ);

                    as.setHeadPose(new EulerAngle(angleX, angleY, 0));
                    as.setBodyPose(new EulerAngle(0, angleY, 0));

                    if (elapsedTicks % 10 == 0) {
                        Arrow arrow = (Arrow) playerWorld.spawnEntity(as.getEyeLocation(), EntityType.ARROW);
                        double d0 = closestEntity.getEyeLocation().getY() - 1.100000023841858;
                        double d1 = closestEntity.getLocation().getX() - as.getLocation().getX();
                        double d2 = d0 - arrow.getLocation().getY();
                        double d3 = closestEntity.getLocation().getZ() - as.getLocation().getZ();
                        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224;
                        Vector velocity = new Vector(d1, d2 + d4, d3);
                        velocity.normalize();
                        velocity.multiply(1.6f);
                        arrow.setVelocity(velocity);

                        playerWorld.playSound(as.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
                    }
                }

                if (elapsedTicks == Integer.MAX_VALUE) {
                    elapsedTicks = 0;
                }

                elapsedTicks++;
            }
        }.runTaskTimer(mischievousChest, 0, 1);
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
