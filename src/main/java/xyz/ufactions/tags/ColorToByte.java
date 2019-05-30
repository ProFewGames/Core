package xyz.ufactions.tags;

import org.bukkit.ChatColor;

public class ColorToByte {

	public static int convert(ChatColor color) {
		if (color == ChatColor.BLACK)
			return 15;
		if (color == ChatColor.DARK_BLUE)
			return 11;
		if (color == ChatColor.DARK_GREEN)
			return 13;
		if (color == ChatColor.DARK_AQUA)
			return 9;
		if (color == ChatColor.DARK_PURPLE)
			return 10;
		if (color == ChatColor.GOLD)
			return 1;
		if (color == ChatColor.GRAY)
			return 8;
		if (color == ChatColor.DARK_GRAY)
			return 7;
		if (color == ChatColor.GREEN)
			return 5;
		if (color == ChatColor.AQUA)
			return 3;
		if (color == ChatColor.RED)
			return 14;
		if (color == ChatColor.LIGHT_PURPLE)
			return 6;
		if (color == ChatColor.YELLOW)
			return 4;
		return 0;
	}
}