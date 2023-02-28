package dev.tf2levi.mischievouschest.commands;

import dev.tf2levi.mischievouschest.MischievousChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BaseCommand implements CommandExecutor {
    private final MischievousChest mischievousChest;

    public BaseCommand(MischievousChest mischievousChest) {
        this.mischievousChest = mischievousChest;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§aCommand executor works!!!");
        return true;
    }
}
