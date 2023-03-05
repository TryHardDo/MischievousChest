package dev.tf2levi.sentryguard.sentry;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Sentry {
    private final UUID owner;
    private final String displayName;
    private final boolean enableDisplayName;
    private List<UUID> friendly;
    private List<UUID> enemy;
    private List<EntityType> whitelist;
    private int radius;
    private int fireRate;
    private ArmorStand sentryBody;
    private Inventory sentryInventory;
    private SentryRunnable sentryRunnable;
    private Sound attackSound;
    private double damageMultiplier;
    private double health; // Todo: Make it work
    private boolean fireAmmo;
    private boolean explosiveAmmo;
    private PotionEffect ammoEffect;
    private Color arrowColor;

    public Sentry(UUID ownerUUID) {
        this.owner = ownerUUID;
        displayName = "Őrtorony";
        enableDisplayName = true;
        friendly = new ArrayList<>();
        enemy = new ArrayList<>();
        whitelist = new ArrayList<>();
        radius = 5;
        fireRate = 100;
        sentryBody = null;
        sentryInventory = null;
        sentryRunnable = null;
        attackSound = Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;

        whitelist.addAll(Arrays.asList(
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
        ));
    }

    public boolean isExplosiveAmmo() {
        return explosiveAmmo;
    }

    public void setExplosiveAmmo(boolean explosiveAmmo) {
        this.explosiveAmmo = explosiveAmmo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnableDisplayName() {
        return enableDisplayName;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public boolean isFireAmmo() {
        return fireAmmo;
    }

    public void setFireAmmo(boolean fireAmmo) {
        this.fireAmmo = fireAmmo;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public PotionEffect getAmmoEffect() {
        return ammoEffect;
    }

    public void setAmmoEffect(PotionEffect ammoEffect) {
        this.ammoEffect = ammoEffect;
    }

    public Color getArrowColor() {
        return arrowColor;
    }

    public void setArrowColor(Color arrowColor) {
        this.arrowColor = arrowColor;
    }

    public Sound getAttackSound() {
        return attackSound;
    }

    public void setAttackSound(Sound attackSound) {
        this.attackSound = attackSound;
    }

    public List<UUID> getFriendly() {
        return friendly;
    }

    public void setFriendly(List<UUID> friendly) {
        this.friendly = friendly;
    }

    public List<UUID> getEnemy() {
        return enemy;
    }

    public void setEnemy(List<UUID> enemy) {
        this.enemy = enemy;
    }

    public List<EntityType> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<EntityType> whitelist) {
        this.whitelist = whitelist;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
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

        sentryBody.setCustomName(displayName);
        sentryBody.setCustomNameVisible(enableDisplayName);
        sentryBody.setInvulnerable(true);
        sentryBody.setArms(true);
        sentryBody.setGravity(false);
        sentryBody.setBasePlate(false);

        EntityEquipment equipment = sentryBody.getEquipment();
        assert equipment != null;

        equipment.setHelmet(new ItemStack(Material.OBSERVER), true);
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE), true);
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS), true);
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS), true);

        // Make sure to never attack the owner.
        friendly.add(owner);
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
        return (20 * 60) / fireRate;
    }
}
