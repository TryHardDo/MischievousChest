package dev.tf2levi.mischievouschest;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class Utils {
    public static void LogStartup(ConsoleCommandSender sender, PluginDescriptionFile descriptionFile) {
        sender.sendMessage(
                "§8+",
                "§8| §7$ > §eMischievousChest | §6" + descriptionFile.getVersion(),
                "§8| §7Developed by: " + String.join("§c, §7", descriptionFile.getAuthors()),
                "§8| §7Supported API version: §e" + descriptionFile.getAPIVersion(),
                "§8| §7Env: §9" + Bukkit.getServer().getVersion(),
                "§8+"
        );
    }
}
