package xyz.ufactions.tags.ui.buttons.colors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.tags.ColorToByte;
import xyz.ufactions.tags.TitleModule;

public class IColorButton extends ShopItem<TitleModule> {

	private ChatColor color;

	public IColorButton(TitleModule plugin, ChatColor color, int position) {
		super(plugin, Material.WOOL, ColorToByte.convert(color), color + a(color.name()), position, "&8Click to apply");
		this.color = color;
	}

	private static String a(String a) {
		a = a.replace("_", " ");
		if (a.contains(" ")) {
			String[] b = a.split(" ");
			b[0] = b[0].substring(0, 1).toUpperCase() + b[0].substring(1).toLowerCase();
			b[1] = b[1].substring(0, 1).toUpperCase() + b[1].substring(1).toLowerCase();
			return b[0] + " " + b[1];
		} else {
			return a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();
		}
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		if (Plugin.getTagManager().colorTag(player, color)) {
			player.closeInventory();
		}
	}
}