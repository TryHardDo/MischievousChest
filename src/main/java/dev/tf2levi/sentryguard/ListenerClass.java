package dev.tf2levi.sentryguard;

import dev.tf2levi.sentryguard.enums.ShutdownCause;
import dev.tf2levi.sentryguard.sentry.Sentry;
import dev.tf2levi.sentryguard.sentry.SentryManager;
import dev.tf2levi.sentryguard.sentry.SentrySettings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class ListenerClass implements Listener {
    private final SentryGuard sentryGuard;
    private final HashMap<UUID, Long> cooldowns;

    public ListenerClass(SentryGuard sentryGuard) {
        this.sentryGuard = sentryGuard;
        this.cooldowns = new HashMap<>();
    }

    @EventHandler
    public void onProjectileImpact(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = ((Arrow) e.getEntity());
        NamespacedKey featureKey = new NamespacedKey(sentryGuard, "explosiveAmmo");
        PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
        if (!persistentDataContainer.has(featureKey, PersistentDataType.BYTE)) {
            return;
        }

        persistentDataContainer.remove(featureKey);

        Location impactLoc = e.getHitBlock() != null ? e.getHitBlock().getLocation() : e.getHitEntity() != null ? e.getHitEntity().getLocation() : null;
        if (impactLoc == null) {
            return;
        }

        World world = impactLoc.getWorld();
        if (world == null) {
            return;
        }

        world.createExplosion(impactLoc, 3f, false, false);
        arrow.remove();
    }

    @EventHandler
    public void onSentryClick(PlayerArmorStandManipulateEvent e) {
        Sentry clicked = SentryManager.getByUUID(e.getRightClicked().getUniqueId());

        if (clicked == null) {
            return;
        }

        Player clicker = e.getPlayer();

        e.setCancelled(true);
        if (!clicked.getOwner().equals(clicker.getUniqueId())) {
            clicker.sendMessage("§cNincs hozzáférésed ehhez az Őrtoronyhoz.");
            return;
        }

        e.getPlayer().openInventory(clicked.getSentryInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory clicked = e.getInventory();
        if (SentryManager.getByInventory(clicked) == null) {
            return;
        }

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.ARROW) {
            e.getWhoClicked().sendMessage("§cCsak Nyíl a megengedett lőszer jelenleg.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Sentry sentry = SentryManager.getByInventory(e.getInventory());

        if (sentry == null) {
            return;
        }

        if (sentry.getSentryRunnable().isCancelled() && sentry.getSentryRunnable().getShutdownCause() == ShutdownCause.OUT_OF_AMMO)
            sentry.startSentry(sentryGuard);
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

        Location clickedLocation = clickedBlock.getRelative(BlockFace.UP).getLocation();

        SentrySettings sentrySettings = new SentrySettings();
        sentrySettings.setRadius(10);
        sentrySettings.setArrowColorFeature(Color.GREEN);
        sentrySettings.setFireRate(150);
        sentrySettings.setFireAmmoFeature(true);
        sentrySettings.setExplosiveAmmoFeature(true);
        sentrySettings.setGodModeFeature(true);
        sentrySettings.setGlowingFeature(true);

        Sentry sentry = new Sentry(placer.getUniqueId(), sentrySettings).spawnSentry(clickedLocation);
        sentry.startSentry(sentryGuard);
    }
}
