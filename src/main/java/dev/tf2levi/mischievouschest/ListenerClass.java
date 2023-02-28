package dev.tf2levi.mischievouschest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerClass implements Listener {
    private final MischievousChest mischievousChest;

    public ListenerClass(MischievousChest mischievousChest) {
        this.mischievousChest = mischievousChest;
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent e) {
        
    }
}
