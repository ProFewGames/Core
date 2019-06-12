package xyz.ufactions.sidekick.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.sidekick.SidekickModule;
import xyz.ufactions.libs.C;

public class RemoveSidekickButton extends ShopItem<SidekickModule> {

	public RemoveSidekickButton(SidekickModule plugin) {
		super(plugin, Material.BARRIER, C.cRed + "Remove Sidekick", 0);
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.removeSidekick(player);
	}
}