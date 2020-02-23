package xyz.ufactions.core;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class EcoManager {

	private static EcoManager instance;

	public static EcoManager getInstance() {
		if (instance == null) {
			instance = new EcoManager();
		}
		return instance;
	}

	private Economy economy;

	private EcoManager() {
		if (!setupEconomy()) {
			System.err.println("No economy installed on the server");
		}
	}

	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return economy.withdrawPlayer(player, amount);
	}

	public EconomyResponse depositMoney(OfflinePlayer player, double amount) {
		return economy.depositPlayer(player, amount);
	}

	public double getBalance(OfflinePlayer player) {
		return economy.getBalance(player);
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}
}