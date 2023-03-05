package dev.tf2levi.mischievouschest;

import dev.tf2levi.mischievouschest.runnable.HomingProjectile;
import dev.tf2levi.mischievouschest.runnable.SentryRunnable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class ListenerClass implements Listener {
    private final MischievousChest mischievousChest;
    private final HashMap<UUID, Long> cooldowns;

    public ListenerClass(MischievousChest mischievousChest) {
        this.mischievousChest = mischievousChest;
        this.cooldowns = new HashMap<>();
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

        if (!e.getPlayer().isSneaking()) {
            return;
        }

        Player placer = e.getPlayer();
        long currentTime = System.currentTimeMillis();
        cooldowns.putIfAbsent(placer.getUniqueId(), currentTime);
        if (cooldowns.get(placer.getUniqueId()) + 200 > currentTime) {
            return;
        }
        cooldowns.put(placer.getUniqueId(), currentTime);

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

        World playerWorld = placer.getWorld();
        Location clickedLocation = clickedBlock.getRelative(BlockFace.UP).getLocation();

        ArmorStand as = (ArmorStand) playerWorld.spawnEntity(clickedLocation.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);

        as.setSmall(false);
        as.setGravity(false);
        as.setBasePlate(false);
        as.setCustomName("§c§lVédelem");
        as.setCustomNameVisible(true);
        as.setInvulnerable(true);

        EntityEquipment equipment = as.getEquipment();
        assert equipment != null;

        equipment.setHelmet(new ItemStack(Material.OBSERVER), true);
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE), true);
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS), true);
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS), true);

        int radius = 10;
        new SentryRunnable(as, playerWorld, radius).runTaskTimer(mischievousChest, 0, 1);
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
