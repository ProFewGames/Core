package xyz.ufactions.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.ufactions.api.Module;

public abstract class InverseButton<PluginType extends Module> extends ShopItem<PluginType> {

	private InverseButton<PluginType> reverse;

	public InverseButton(PluginType plugin, Material material, int data, String name, int position,
			InverseButton<PluginType> reverseButton, String... lore) {
		super(plugin, material, data, name, position, lore);

		this.reverse = reverseButton;
	}

	public InverseButton(PluginType plugin, Material material, int data, String name, int position, String... lore) {
		super(plugin, material, data, name, position, lore);
	}

	public boolean canInverse(Player player) {
		return true;
	}

	public void setReverse(InverseButton<PluginType> reverse) {
		this.reverse = reverse;
	}

	public InverseButton<PluginType> getReverse() {
		return reverse;
	}
}