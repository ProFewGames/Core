package xyz.ufactions.crates;

import org.bukkit.entity.Player;

public abstract class CrateHook {

	private String[] placeholders;

	public CrateHook(String... placeholders) {
		this.placeholders = placeholders;
		CratesModule.initializeHook(this);
	}

	public abstract void execute(String placeholder, String[] args, Player player);

	public String[] getPlaceholders() {
		return placeholders;
	}

	protected String convert(String[] args) {
		String string = "";
		for (int i = 0; i < args.length; i++) {
			if (string.equals("")) {
				string = args[i];
			} else {
				string += " " + args[i];
			}
		}
		return string.trim();
	}
}