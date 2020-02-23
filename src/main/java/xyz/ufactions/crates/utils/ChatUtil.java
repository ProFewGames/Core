package xyz.ufactions.crates.utils;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class ChatUtil {

	public static String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(cc("&8(&4&lCC&8) &7" + message));
	}

	public static String listToStr(List<String> list) {
		return listToStr(", ", list);
	}

	public static String listToStr(String splitter, List<String> list) {
		String str = "";
		for (String content : list) {
			if (str.equals("")) {
				str = content;
			} else {
				str += splitter + content;
			}
		}
		return str;
	}

	public static void invalidCrate(CommandSender sender) {
		sendMessage(sender, "&cYou have entered an invalid crate name.");
	}

	public static String description(String string, String description) {
		return cc("&7" + string + " &c" + description);
	}

	public static void error(CommandSender sender, String message) {
		sendMessage(sender, "&c" + message);
	}

	public static void success(CommandSender sender, String message) {
		sendMessage(sender, "&a" + message);
	}

	public static void noPlayer(CommandSender sender) {
		error(sender, "You must be a player in-game to do this command.");
	}

	public static void validCrate(CommandSender sender) {
		sendMessage(sender, "&cThis crate already exist.");
	}

	public static String getName(Material material) {
		String name = material.name();
		name = name.replace("_", " ");
		name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		return name;
	}
}
