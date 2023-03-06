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
    private ShutdownCause shutdownCause = null;

    public SentryRunnable(Sentry sentry) {
        this.sentry = sentry;
        elapsedTicks = 0;
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
        SentrySettings sentrySettings = sentry.getSentrySettings();
        int radius = sentrySettings.getRadius();

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
            nearbyEntities.removeIf(entity -> sentrySettings.getEntityTypeWhitelist().contains(entity.getType())
                    || sentrySettings.getFriendlyPlayers().contains(entity.getUniqueId()));

            Mob closestEntity = null;
            double closestDistance = Double.MAX_VALUE;

            for (Entity entity : nearbyEntities) {
                if (!(entity instanceof Mob)) {
                    continue;
                }

                Mob foundMob = (Mob) entity;

                if (!entity.isValid()) {
                    continue;
                }

                double distance = foundMob.getLocation().distance(as.getLocation());
                if (distance <= radius && distance < closestDistance) {
                    if (hasLineOfSight(foundMob, as)) {
                        closestEntity = foundMob;
                        closestDistance = distance;
                    }
                }
            }

            if (closestEntity == null) {
                return;
            }

            as.setHeadPose(new EulerAngle(Utils.calculatePitch(as.getEyeLocation(), closestEntity.getEyeLocation()), 0, 0));
            as.setRotation((float) Math.toDegrees(Utils.calculateYaw(as.getEyeLocation(), closestEntity.getEyeLocation())), 0);

            double d0 = closestEntity.getEyeLocation().getY() - 1.100000023841858;
            double d1 = closestEntity.getLocation().getX() - as.getLocation().getX();
            double d2 = d0 - as.getEyeLocation().getY();
            double d3 = closestEntity.getLocation().getZ() - as.getLocation().getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.20000000298023224;

            Vector velocity = new Vector(d1, d2 + d4, d3);
            velocity.normalize();
            velocity.multiply(1.6f);

            Arrow bullet = (Arrow) sentryWorld.spawnEntity(as.getEyeLocation(), EntityType.ARROW);

            bullet.setShooter(as);
            if (sentrySettings.getAmmoEffectFeature() != null)
                bullet.addCustomEffect(sentrySettings.getAmmoEffectFeature(), true);
            bullet.setColor(sentrySettings.getArrowColorFeature());
            bullet.setDamage(bullet.getDamage() * sentrySettings.getDamageMultiplier());
            bullet.setFireTicks(sentrySettings.isFireAmmoFeature() ? 20 * 60 : 0);
            if (sentrySettings.isExplosiveAmmoFeature())
                bullet.getPersistentDataContainer().set(new NamespacedKey(SentryGuard.getInstance(), "explosiveAmmo"), PersistentDataType.BYTE, (byte) 1);
            bullet.setVelocity(velocity);

            inventory.removeItem(ammo);
            sentryWorld.playSound(as.getLocation(), sentrySettings.getAttackSound(), 1, 1);
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

    private boolean hasLineOfSight(Mob mob, ArmorStand as) {
        Vector direction = mob.getEyeLocation().toVector().subtract(as.getEyeLocation().toVector());
        double distance = direction.length();
        direction.normalize();
        return as.getWorld().rayTraceBlocks(as.getEyeLocation(), direction, distance, FluidCollisionMode.NEVER, true) == null;
    }
}
