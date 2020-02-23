package xyz.ufactions.crates.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class UtilChat {

	public static String strip(String string) {
		string = cc(string);
		string = ChatColor.stripColor(string);
		string = string.replaceAll(" ", "_");
		return string;
	}

	public static String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void message(CommandSender sender, String message) {
		message(sender, message, true);
	}

	public static void message(CommandSender sender, String message, boolean color) {
		if (color)
			sender.sendMessage(cc(message));
		else
			sender.sendMessage(message);
	}

	public static void broadcast(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			message(player, message);
		}
	}
}