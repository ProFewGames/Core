package xyz.ufactions.chatcolor.ui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.libs.C;

public class GreenButton extends OrderingButton<ColorModule> {

	public GreenButton(ColorModule plugin) {
		super(plugin, Material.WOOL, 5, C.cGreen + "Green", "&8Click to equip");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.setColor(player, ChatColor.GREEN);
		player.sendMessage(C.cYellow + "You equipped the chat color: " + C.cGreen + "Green");
	}
}