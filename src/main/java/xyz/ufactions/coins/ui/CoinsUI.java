package xyz.ufactions.coins.ui;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.libs.C;
import xyz.ufactions.shop.Shop;

public class CoinsUI extends Shop {
	
	public CoinsUI(JavaPlugin plugin) {
		super(plugin, C.cGold + "Coin Shop", 36, ShopFiller.NONE);
	}
}