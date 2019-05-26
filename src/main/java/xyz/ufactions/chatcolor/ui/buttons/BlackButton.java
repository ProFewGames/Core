package xyz.ufactions.chatcolor.ui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.libs.C;

public class BlackButton extends OrderingButton<ColorModule> {

	public BlackButton(ColorModule plugin) {
		super(plugin, Material.WOOL, 15, "&0Black", "&8Click to equip");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.setColor(player, ChatColor.BLACK);
		player.sendMessage(C.cYellow + "You equipped the chat color: " + C.cBlack + "Black");
	}
}