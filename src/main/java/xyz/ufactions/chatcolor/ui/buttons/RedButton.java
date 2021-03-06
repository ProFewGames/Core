package xyz.ufactions.chatcolor.ui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.libs.C;

public class RedButton extends OrderingButton<ColorModule> {

	public RedButton(ColorModule plugin) {
		super(plugin, Material.WOOL, 14, C.cRed + "Red", "&8Click to equip");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.setColor(player, ChatColor.RED);
		player.sendMessage(C.cYellow + "You equipped the chat color: " + C.cRed + "Red");
	}
}