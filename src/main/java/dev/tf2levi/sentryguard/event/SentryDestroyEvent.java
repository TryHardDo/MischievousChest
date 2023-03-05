package dev.tf2levi.sentryguard.event;

import dev.tf2levi.sentryguard.sentry.Sentry;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SentryDestroyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Sentry sentry;
    private boolean canceled;

    public SentryDestroyEvent(Sentry toBeDestroyed) {
        canceled = false;
        sentry = toBeDestroyed;
    }

    public static HandlerList getHandlersList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }
}
