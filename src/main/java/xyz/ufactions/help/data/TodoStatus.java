package xyz.ufactions.help.data;

import net.md_5.bungee.api.ChatColor;

public enum TodoStatus {

	DONE("&a&lDONE"), IN_PROGRESS("&e&lIn Progress"), HALTED("&c&lHalted"), WAITING("&7&lWaiting");

	private final String name;

	private TodoStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return ChatColor.translateAlternateColorCodes('&', name);
	}
}