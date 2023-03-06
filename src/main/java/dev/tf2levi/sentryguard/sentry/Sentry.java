package dev.tf2levi.sentryguard.sentry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class Sentry {
    private final UUID owner;
    private ArmorStand sentryBody;
    private Inventory sentryInventory;
    private SentryRunnable sentryRunnable;
    private SentrySettings sentrySettings;

    public Sentry(UUID ownerUUID, SentrySettings sentrySettings) {
        this.owner = ownerUUID;
        this.sentryBody = null;
        this.sentryInventory = null;
        this.sentryRunnable = null;
        this.sentrySettings = sentrySettings;

        /*whitelist.addAll(Arrays.asList(
                EntityType.VILLAGER,
                EntityType.ALLAY,
                EntityType.IRON_GOLEM,
                EntityType.BEE,
                EntityType.DOLPHIN,
                EntityType.DONKEY,
                EntityType.HORSE,
                EntityType.SNOWMAN,
                EntityType.TRADER_LLAMA,
                EntityType.WANDERING_TRADER,
                EntityType.WOLF,
                EntityType.TURTLE,
                EntityType.PARROT,
                EntityType.PANDA
        ));*/
    }

    public SentrySettings getSentrySettings() {
        return sentrySettings;
    }

    public void setSentrySettings(SentrySettings sentrySettings) {
        this.sentrySettings = sentrySettings;
    }

    public ArmorStand getSentryBody() {
        return sentryBody;
    }

    public void setSentryBody(ArmorStand sentryBody) {
        this.sentryBody = sentryBody;
    }

    public Inventory getSentryInventory() {
        return sentryInventory;
    }

    public void setSentryInventory(Inventory sentryInventory) {
        this.sentryInventory = sentryInventory;
    }

    public SentryRunnable getSentryRunnable() {
        return sentryRunnable;
    }

    public void setSentryRunnable(SentryRunnable sentryRunnable) {
        this.sentryRunnable = sentryRunnable;
    }

    public UUID getOwner() {
        return owner;
    }

    public Sentry spawnSentry(Location location) {
        if (sentryBody != null) {
            if (sentryBody.isValid()) {
                return this;
            }
        }

        World spawnWorld = location.getWorld();

        if (spawnWorld == null) {
            return null;
        }

        // Centering loc
        sentryBody = (ArmorStand) spawnWorld.spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        sentryInventory = Bukkit.createInventory(null, InventoryType.CHEST, "§cLőszer tartó");

        sentryBody.setCustomNameVisible(sentrySettings.isVisibleDisplayNameFeature());
        if (sentrySettings.getDisplayName() != null) sentryBody.setCustomName(sentrySettings.getDisplayName());
        sentryBody.setInvulnerable(sentrySettings.isGodModeFeature());
        sentryBody.setArms(sentrySettings.isShowArms());
        sentryBody.setGravity(sentrySettings.isAntiGravityFeature());
        sentryBody.setGlowing(sentrySettings.isGlowingFeature());
        sentryBody.setBasePlate(false);

        EntityEquipment equipment = sentryBody.getEquipment();
        assert equipment != null;

        equipment.setHelmet(new ItemStack(Material.OBSERVER), true);
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE), true);
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS), true);
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS), true);

        // Make sure to never attack the owner.
        sentrySettings.getFriendlyPlayers().add(owner);
        SentryManager.registerSentry(this);

        return this;
    }

    public boolean isValid() {
        return sentryBody.isValid();
    }

    public Sentry removeSentry() {
        // Body removal
        sentryBody.remove();
        sentryBody = null;

        // Inventory refund
        ItemStack[] containedItems = sentryInventory.getContents();

        // Before the refund we clear the inv. The items are already sored in heap.
        sentryInventory.clear();

        Player ownerPlayer = Bukkit.getPlayer(owner);
        if (ownerPlayer != null) {
            ownerPlayer.getInventory().addItem(containedItems).forEach(((integer, itemStack) -> {
                Location playerLoc = ownerPlayer.getLocation();

                itemStack.setAmount(integer);
                ownerPlayer.getWorld().dropItem(playerLoc, itemStack);
            }));
        }

        // Sentry task canceling
        if (!sentryRunnable.isCancelled()) sentryRunnable.cancel();
        sentryRunnable = null;
        SentryManager.unregisterSentry(this);

        return this;
    }

    public void startSentry(Plugin plugin) {
        if (!sentryBody.isValid()) {
            // Todo: Error handling.
            return;
        }

        sentryRunnable = new SentryRunnable(this);
        sentryRunnable.runTaskTimer(plugin, 0, 1);
    }

    public void pauseSentry() {
        sentryRunnable.cancel();
    }

    public int getFireRateInTick() {
        return (20 * 60) / sentrySettings.getFireRate();
    }
}
