package xyz.ufactions.tags.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.libs.C;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.tags.TitleModule;

public class RemoveTitleButton extends ShopItem<TitleModule> {

	public RemoveTitleButton(TitleModule plugin) {
		super(plugin, Material.BARRIER, C.cRed + "Remove Title", 0);
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.getTagManager().enableTag(player, "");
	}
}