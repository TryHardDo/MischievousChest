package dev.tf2levi.sentryguard.sentry;

import dev.tf2levi.sentryguard.SentryGuard;
import dev.tf2levi.sentryguard.Utils;
import dev.tf2levi.sentryguard.enums.ShutdownCause;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SentryRunnable extends BukkitRunnable {
    private final Sentry sentry;
    int elapsedTicks;
    private Location lastTargetLoc;
    private boolean hadLineOfSight = false;

    private ShutdownCause shutdownCause = null;

    public SentryRunnable(Sentry sentry) {
        this.sentry = sentry;
        elapsedTicks = 0;
        lastTargetLoc = null;
    }

    @Override
    public void run() {
        // If the body was destroyed or removed we stop the targeting scheduler
        if (!sentry.getSentryBody().isValid()) {
            this.cancel();
            this.shutdownCause = ShutdownCause.BODY_DESTROYED;
            return;
        }

        ArmorStand as = sentry.getSentryBody();
        World sentryWorld = as.getWorld();
        Player ownerPlayer = Bukkit.getPlayer(sentry.getOwner());
        int tickSpeed = sentry.getFireRateInTick();
        int radius = sentry.getRadius();

        // Fire rate impacts the calculation speed too. If the fire rate is low, the body pose recalculation is also slow. I will make separate in the future.
        if (elapsedTicks % tickSpeed == 0) {
            // First we have to check does the machine has ammo because it can save a lot of memory and cpu performance.
            Inventory inventory = sentry.getSentryInventory();
            ItemStack ammo = new ItemStack(Material.ARROW, 1);
            if (!inventory.containsAtLeast(ammo, 1)) {
                this.cancel();
                this.shutdownCause = ShutdownCause.OUT_OF_AMMO;

                if (ownerPlayer != null) {
                    ownerPlayer.sendMessage("§cKifogyott a lőszer a következő Őrtoronyból: §e" + as.getLocation());
                }

                return;
            }

            Collection<Entity> nearbyEntities = sentryWorld.getNearbyEntities(as.getLocation(), radius, radius, radius);

            // Filter entities which should not be attacked including friendly uuid-s which can be player or any entity.
            nearbyEntities.removeIf(entity -> sentry.getWhitelist().contains(entity.getType())
                    || sentry.getFriendly().contains(entity.getUniqueId()));

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

            if (lastTargetLoc != null && lastTargetLoc.equals(closestEntity.getLocation()) && !hadLineOfSight) {
                return;
            }

            double d0 = closestEntity.getEyeLocation().getY() - 1.100000023841858;
            double d1 = closestEntity.getLocation().getX() - as.getLocation().getX();
            double d2 = d0 - as.getEyeLocation().getY();
            double d3 = closestEntity.getLocation().getZ() - as.getLocation().getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224;

            Vector velocity = new Vector(d1, d2 + d4, d3);
            velocity.normalize();
            velocity.multiply(1.6f);
            lastTargetLoc = closestEntity.getLocation();

            if (!Utils.hasLineOfSight(sentryWorld, velocity)) {
                hadLineOfSight = false;
                return;
            }

            hadLineOfSight = true;
            Arrow bullet = (Arrow) sentryWorld.spawnEntity(as.getEyeLocation(), EntityType.ARROW);

            bullet.setShooter(as);
            if (sentry.getAmmoEffect() != null) bullet.addCustomEffect(sentry.getAmmoEffect(), true);
            bullet.setColor(sentry.getArrowColor());
            bullet.setDamage(bullet.getDamage() * sentry.getDamageMultiplier());
            bullet.setFireTicks(sentry.isFireAmmo() ? 20 * 60 : 0);
            if (sentry.isExplosiveAmmo())
                bullet.getPersistentDataContainer().set(new NamespacedKey(SentryGuard.getInstance(), "explosiveAmmo"), PersistentDataType.BYTE, (byte) 1);
            bullet.setVelocity(velocity);

            inventory.removeItem(ammo);
            sentryWorld.playSound(as.getLocation(), sentry.getAttackSound(), 1, 1);
        }

        if (elapsedTicks == Integer.MAX_VALUE) {
            elapsedTicks = 0;
            return;
        }

        elapsedTicks++;
    }

    public ShutdownCause getShutdownCause() {
        return shutdownCause;
    }

    public void setShutdownCause(ShutdownCause shutdownCause) {
        this.shutdownCause = shutdownCause;
    }
}
