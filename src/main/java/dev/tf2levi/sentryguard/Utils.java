package dev.tf2levi.sentryguard;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Utils {
    public static void LogStartup(ConsoleCommandSender sender, PluginDescriptionFile descriptionFile) {
        sender.sendMessage(
                "§8+",
                "§8| §7$ > §eSentryGuard | §6" + descriptionFile.getVersion(),
                "§8| §7Developed by: " + String.join("§c, §7", descriptionFile.getAuthors()),
                "§8| §7Supported API version: §e" + descriptionFile.getAPIVersion(),
                "§8| §7Env: §9" + Bukkit.getServer().getVersion(),
                "§8+"
        );
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

    public static boolean hasLineOfSight(Entity entity, Location bodyLoc) {
        World world = entity.getWorld();
        Location entityLocation = entity.getLocation();
        Vector direction = entityLocation.toVector().subtract(bodyLoc.toVector()).normalize();
        double distance = bodyLoc.distance(entityLocation);
        RayTraceResult result = world.rayTrace(bodyLoc, direction, distance, FluidCollisionMode.NEVER, true, 0.0, null);

        return result == null || result.getHitEntity() == entity;
    }

}
