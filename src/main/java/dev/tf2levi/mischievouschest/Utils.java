package dev.tf2levi.mischievouschest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

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

    public static EulerAngle calculateArmorStandHeadRotation(Location eyeLocation, Location targetLocation) {
        Vector direction = targetLocation.toVector().subtract(eyeLocation.toVector()).normalize();
        double pitch = Math.asin(-direction.getY());
        double yaw = Math.atan2(-direction.getX(), direction.getZ());
        return new EulerAngle(pitch, yaw, 0);
    }

    public static double calculatePitch(Location sourceLocation, Location targetLocation) {
        double dx = targetLocation.getX() - sourceLocation.getX();
        double dy = targetLocation.getY() - sourceLocation.getY();
        double dz = targetLocation.getZ() - sourceLocation.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return -Math.asin(dy / distance);
    }

    public static double calculateYaw(Location sourceLocation, Location targetLocation) {
        double dx = targetLocation.getX() - sourceLocation.getX();
        double dz = targetLocation.getZ() - sourceLocation.getZ();
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        double yaw = Math.atan2(dx / distanceXZ, dz / distanceXZ);
        if (yaw < 0) {
            yaw += 2 * Math.PI;
        }
        return -yaw;
    }
}
