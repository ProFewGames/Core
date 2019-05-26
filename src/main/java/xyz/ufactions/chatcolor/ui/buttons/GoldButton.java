package xyz.ufactions.chatcolor.ui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.shop.OrderingButton;
import xyz.ufactions.libs.C;

public class GoldButton extends OrderingButton<ColorModule> {

	public GoldButton(ColorModule plugin) {
		super(plugin, Material.WOOL, 1, C.cGold + "Gold", "&8Click to equip");
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		Plugin.setColor(player, ChatColor.GOLD);
		player.sendMessage(C.cYellow + "You equipped the chat color: " + C.cGold + "Gold");
	}
}