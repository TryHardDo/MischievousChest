package dev.tf2levi.sentryguard.sentry;

import dev.tf2levi.sentryguard.SentryGuard;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SentryManager {
    private static List<Sentry> activeSentries = new ArrayList<>();
    private final SentryGuard sentryGuard;

    public SentryManager(SentryGuard sentryGuard) {
        this.sentryGuard = sentryGuard;
    }

    public static Sentry getByInventory(Inventory inventory) {
        return activeSentries.stream().filter(sentry -> sentry.getSentryInventory().equals(inventory)).findFirst().orElse(null);
    }

    public static Sentry getByUUID(UUID bodyID) {
        return activeSentries.stream().filter(sentry -> sentry.getSentryBody().getUniqueId().equals(bodyID)).findFirst().orElse(null);
    }

    public static Sentry getByTaskId(int taskId) {
        return activeSentries.stream().filter(sentry -> sentry.getSentryRunnable().getTaskId() == taskId).findFirst().orElse(null);
    }

    public static void eraseCache() {
        activeSentries.clear();
    }

    public static void setCache(Collection<Sentry> sentries) {
        activeSentries = new ArrayList<>(sentries);
    }

    public static void registerSentry(Sentry sentry) {
        activeSentries.add(sentry);
    }

    public static void unregisterSentry(Sentry sentry) {
        activeSentries.remove(sentry);
    }
}