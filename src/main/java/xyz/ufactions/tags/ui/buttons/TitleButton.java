package xyz.ufactions.tags.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.ufactions.libs.C;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.tags.TitleModule;

public class TitleButton extends OrderingButton<TitleModule> {

	private String name;

	public TitleButton(TitleModule plugin, String name) {
		super(plugin, Material.BANNER, 14, name);

		ItemStack item = new ItemStack(Material.BANNER, 1, (byte) 14);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(C.cWhite + name);
		item.setItemMeta(meta);

		setItem(item);

		this.name = name;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.getTagManager().enableTag(player, name);
	}
}