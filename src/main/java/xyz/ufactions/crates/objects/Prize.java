package xyz.ufactions.crates.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import xyz.ufactions.libs.ItemBuilder;

public class Prize {

	private List<String> commands;
	private List<String> lore;
	private ItemStack item;
	private String displayName;
	private boolean isGlowing;
	private double chance;
	private int itemID;
	private int data;
	private int amount;

	public Prize(FileConfiguration config, String prizeName) {
		try {
			String path = "Prizes." + prizeName;
			this.commands = config.getStringList(path + ".commands");
			this.lore = config.getStringList(path + ".lore");
			this.displayName = config.getString(path + ".display name");
			this.isGlowing = config.getBoolean(path + ".glow");
			this.chance = config.getDouble(path + ".chance");
			this.itemID = config.getInt(path + ".item id");
			this.data = config.getInt(path + ".item data");
			this.amount = config.getInt(path + ".item amount");
			List<String> finLore = new ArrayList<>();
			for (String line : lore) {
				finLore.add(line.replace("%chance%", chance + "%"));
			}
			this.item = new ItemBuilder(itemID, data).amount(amount).name(displayName).lore(finLore).glow(isGlowing)
					.build();
		} catch (Exception e) {
			System.out.println("There was an error while generating a prize from '" + config.getName() + "'");
			e.printStackTrace();
		}
	}

	public boolean isGlowing() {
		return isGlowing;
	}

	public int getAmount() {
		return amount;
	}

	public double getChance() {
		return chance;
	}

	public List<String> getCommands() {
		return commands;
	}

	public int getData() {
		return data;
	}

	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}

	public ItemStack getItem() {
		return item;
	}

	public int getItemID() {
		return itemID;
	}

	public List<String> getLore() {
		return lore;
	}
}
