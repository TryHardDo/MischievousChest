package dev.tf2levi.sentryguard.sentry;

import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SentrySettings {
    private Sound attackSound = Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;
    private double damageMultiplier = 1.0;
    private double maxHealth = 20;
    private String displayName = null;
    private List<UUID> friendlyPlayers = new ArrayList<>();
    private List<UUID> enemyPlayers = new ArrayList<>();
    private List<EntityType> entityTypeWhitelist = new ArrayList<>();
    private int radius = 5;
    private int fireRate = 100;
    private int storageSpace = 9;
    private boolean autoPickupFeature = false;
    private boolean explosiveAmmoFeature = false;
    private boolean visibleDisplayNameFeature = false;
    private boolean fireAmmoFeature = false;
    private boolean godModeFeature = false;
    private boolean antiGravityFeature = true;
    private boolean glowingFeature;
    private PotionEffect ammoEffectFeature = null;
    private Color arrowColorFeature = null;
    private EntityEquipment sentryEquipment = null;
    private boolean showArms = true;
    private EntityType ammoType = EntityType.ARROW;

    public SentrySettings() {

    }

    public Sound getAttackSound() {
        return attackSound;
    }

    public void setAttackSound(Sound attackSound) {
        this.attackSound = attackSound;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<UUID> getFriendlyPlayers() {
        return friendlyPlayers;
    }

    public void setFriendlyPlayers(List<UUID> friendlyPlayers) {
        this.friendlyPlayers = friendlyPlayers;
    }

    public List<UUID> getEnemyPlayers() {
        return enemyPlayers;
    }

    public void setEnemyPlayers(List<UUID> enemyPlayers) {
        this.enemyPlayers = enemyPlayers;
    }

    public List<EntityType> getEntityTypeWhitelist() {
        return entityTypeWhitelist;
    }

    public void setEntityTypeWhitelist(List<EntityType> entityTypeWhitelist) {
        this.entityTypeWhitelist = entityTypeWhitelist;
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

    public int getStorageSpace() {
        return storageSpace;
    }

    public void setStorageSpace(int storageSpace) {
        this.storageSpace = storageSpace;
    }

    public boolean isAutoPickupFeature() {
        return autoPickupFeature;
    }

    public void setAutoPickupFeature(boolean autoPickupFeature) {
        this.autoPickupFeature = autoPickupFeature;
    }

    public boolean isExplosiveAmmoFeature() {
        return explosiveAmmoFeature;
    }

    public void setExplosiveAmmoFeature(boolean explosiveAmmoFeature) {
        this.explosiveAmmoFeature = explosiveAmmoFeature;
    }

    public boolean isVisibleDisplayNameFeature() {
        return visibleDisplayNameFeature;
    }

    public void setVisibleDisplayNameFeature(boolean visibleDisplayNameFeature) {
        this.visibleDisplayNameFeature = visibleDisplayNameFeature;
    }

    public boolean isFireAmmoFeature() {
        return fireAmmoFeature;
    }

    public void setFireAmmoFeature(boolean fireAmmoFeature) {
        this.fireAmmoFeature = fireAmmoFeature;
    }

    public boolean isGodModeFeature() {
        return godModeFeature;
    }

    public void setGodModeFeature(boolean godModeFeature) {
        this.godModeFeature = godModeFeature;
    }

    public boolean isAntiGravityFeature() {
        return antiGravityFeature;
    }

    public void setAntiGravityFeature(boolean antiGravityFeature) {
        this.antiGravityFeature = antiGravityFeature;
    }

    public boolean isGlowingFeature() {
        return glowingFeature;
    }

    public void setGlowingFeature(boolean glowingFeature) {
        this.glowingFeature = glowingFeature;
    }

    public PotionEffect getAmmoEffectFeature() {
        return ammoEffectFeature;
    }

    public void setAmmoEffectFeature(PotionEffect ammoEffectFeature) {
        this.ammoEffectFeature = ammoEffectFeature;
    }

    public Color getArrowColorFeature() {
        return arrowColorFeature;
    }

    public void setArrowColorFeature(Color arrowColorFeature) {
        this.arrowColorFeature = arrowColorFeature;
    }

    public EntityEquipment getSentryEquipment() {
        return sentryEquipment;
    }

    public void setSentryEquipment(EntityEquipment sentryEquipment) {
        this.sentryEquipment = sentryEquipment;
    }

    public boolean isShowArms() {
        return showArms;
    }

    public void setShowArms(boolean showArms) {
        this.showArms = showArms;
    }

    public EntityType getAmmoType() {
        return ammoType;
    }

    public void setAmmoType(EntityType ammoType) {
        this.ammoType = ammoType;
    }
}
