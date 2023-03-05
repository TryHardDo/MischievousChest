package dev.tf2levi.sentryguard.commands;

import dev.tf2levi.sentryguard.SentryGuard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BaseCommand implements CommandExecutor {
    private final SentryGuard sentryGuard;

    public BaseCommand(SentryGuard sentryGuard) {
        this.sentryGuard = sentryGuard;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§aCommand executor works!!!");
        return true;
    }
}
