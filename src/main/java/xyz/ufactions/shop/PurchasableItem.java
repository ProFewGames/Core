package xyz.ufactions.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilMath;
import xyz.ufactions.libs.UtilPlayer;

public abstract class PurchasableItem<PluginType extends Module> extends ShopItem<PluginType> {

	private CurrencyType type;
	private int cost = 0;
	private String name;
	private String[] lore;

	protected boolean closeInventory = true;
	
	private static Economy economy;

	public static void setEconomy(Economy economy) {
		PurchasableItem.economy = economy;
	}

	public PurchasableItem(PluginType plugin, CurrencyType type, int cost, Material material, String name, int position,
			String... lore) {
		super(plugin, material, name, position, parseLore(type, cost, lore));

		this.type = type;
		this.cost = cost;
		this.name = name;
		this.lore = lore;
	}

	public PurchasableItem(PluginType plugin, CurrencyType type, int cost, Material material, String name,
			int position) {
		super(plugin, material, name, position, costLore(type, cost));

		this.type = type;
		this.cost = cost;
		this.name = name;
		this.lore = new String[0];
	}

	private static List<String> parseLore(CurrencyType type, int cost, String... loreParam) {
		List<String> lore = new ArrayList<>();
		for (String l : loreParam) {
			lore.add(l);
		}
		lore.add(costLore(type, cost));
		return lore;
	}

	private static String costLore(CurrencyType type, int cost) {
		if (type == CurrencyType.MONEY) {
			return C.cWhite + "$" + UtilMath.fixMoney(cost);
		} else {
			return "";
		}
	}

	public boolean additionalPurchaseChecks(Player player) {
		return true;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		if (type == CurrencyType.MONEY) {
			if (economy == null) {
				System.out.println("Economy is not setup with the plugin.");
				UtilPlayer.message(player, F.main("Shop", C.cRed
						+ "Economy is not setup with the plugin. If you believe this is an error please contact a server administrator."));
				return;
			}
			EconomyResponse response = economy.withdrawPlayer(player, cost);
			if (response.transactionSuccess()) {
				if (additionalPurchaseChecks(player)) {
					purchaseSuccessful(player, clickType);
					if (closeInventory)
						player.closeInventory();
				} else {
					return;
				}
			} else {
				UtilPlayer.message(player, C.cRed + "You don't have enough money to purchase this item.");
				purchaseDeclined(player, clickType);
				return;
			}
		}
		// TODO Add more cost types
	}

	public String[] getLore() {
		return lore;
	}

	public String getName() {
		return name;
	}

	public String getCostFormatted() {
		return UtilMath.fixMoney(cost);
	}

	public int getCost() {
		return cost;
	}

	public CurrencyType getType() {
		return type;
	}

	public void purchaseDeclined(Player player, ClickType clickType) {
	}

	public abstract void purchaseSuccessful(Player player, ClickType clickType);
}