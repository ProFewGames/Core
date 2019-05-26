package xyz.ufactions.chatcolor.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.chatcolor.ui.buttons.*;
import xyz.ufactions.shop.IButton;
import xyz.ufactions.shop.PanelButton;
import xyz.ufactions.shop.Shop;

public class ColorUI {

	private Shop shop;
	private ColorModule plugin;

	public ColorUI(Player player, ColorModule plugin) {
		this.plugin = plugin;
		buildPage(player);
		shop.openInventory(player);
	}

	private void buildPage(Player player) {
		List<IButton> items = new ArrayList<>();
		if (player.hasPermission("chatcolor.black")) {
			items.add(new BlackButton(plugin));
		}
		if (player.hasPermission("chatcolor.darkblue")) {
			items.add(new DarkBlueButton(plugin));
		}
		if (player.hasPermission("chatcolor.darkgreen")) {
			items.add(new DarkGreenButton(plugin));
		}
		if (player.hasPermission("chatcolor.darkaqua")) {
			items.add(new DarkAquaButton(plugin));
		}
		if (player.hasPermission("chatcolor.darkpurple")) {
			items.add(new DarkPurpleButton(plugin));
		}
		if (player.hasPermission("chatcolor.gold")) {
			items.add(new GoldButton(plugin));
		}
		if (player.hasPermission("chatcolor.gray")) {
			items.add(new GrayButton(plugin));
		}
		if (player.hasPermission("chatcolor.darkgray")) {
			items.add(new DarkGrayButton(plugin));
		}
		if (player.hasPermission("chatcolor.green")) {
			items.add(new GreenButton(plugin));
		}
		if (player.hasPermission("chatcolor.aqua")) {
			items.add(new AquaButton(plugin));
		}
		if (player.hasPermission("chatcolor.red")) {
			items.add(new RedButton(plugin));
		}
		if (player.hasPermission("chatcolor.pink")) {
			items.add(new PinkButton(plugin));
		}
		if (player.hasPermission("chatcolor.yellow")) {
			items.add(new YellowButton(plugin));
		}
		items.add(new WhiteButton(plugin));
		items.add(new PanelButton(0));
		items.add(new PanelButton(8));
		int length = round(items.size());
		if (length == 18) {
			items.add(new PanelButton(9));
			items.add(new PanelButton(17));
		}
		this.shop = new Shop(plugin.getPlugin(), "Color", length, Shop.ShopFiller.NONE, items);
	}

	private int round(int num) {
		return ((((num / 9) + ((num % 9 == 0) ? 0 : 1)) * 9));
	}
}