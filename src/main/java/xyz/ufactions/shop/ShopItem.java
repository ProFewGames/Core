package xyz.ufactions.shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.ItemBuilder;

public abstract class ShopItem<PluginType extends Module> implements IButton {

	protected ItemStack item;
	private int position;
	protected PluginType Plugin;

	public ShopItem(PluginType plugin, Material material, String name, int position, List<String> lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, String name, int position) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, int data, String name, int position) {
		Plugin = plugin;
		this.item = new ItemBuilder(material, data).name(name).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, String name, int position, String... lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, int data, String name, int position, String... lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material, data).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, int position, ItemStack item) {
		Plugin = plugin;
		this.item = item;
		this.position = position;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getPosition() {
		return position;
	}
}