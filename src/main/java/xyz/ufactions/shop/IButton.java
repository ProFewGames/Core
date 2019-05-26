package xyz.ufactions.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract interface IButton {

	/**
	 * @return The ItemStack that will show up in the shop, use this method in
	 *         your button class to add an updater.
	 */
	public abstract ItemStack getItem();

	public abstract void onClick(Player player, ClickType clickType);
}