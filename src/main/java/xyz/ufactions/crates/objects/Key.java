package xyz.ufactions.crates.objects;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.libs.ItemBuilder;

public class Key {

	private String displayName;
	private List<String> lore;
	private ItemStack item;
	private boolean glow;
	private int data;
	private int id;

	public Key(FileConfiguration config) {
		this.displayName = config.getString("Key.display name");
		this.lore = config.getStringList("Key.lore");
		this.glow = config.getBoolean("Key.glow");
		this.data = config.getInt("Key.data");
		this.id = config.getInt("Key.id");
		this.item = new ItemBuilder(id, data).name(displayName).glow(glow).lore(lore).build();
	}

	public boolean isGlow() {
		return glow;
	}

	public int getData() {
		return data;
	}

	public int getId() {
		return id;
	}

	public ItemStack getItem() {
		return item;
	}
}
