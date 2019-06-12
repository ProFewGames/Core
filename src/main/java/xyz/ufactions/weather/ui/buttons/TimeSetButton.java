package xyz.ufactions.weather.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.ItemBuilder;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.weather.WeatherModule;
import xyz.ufactions.weather.ui.TimeSetUI;

public class TimeSetButton extends ShopItem<WeatherModule> {

	public TimeSetButton(WeatherModule module) {
		super(module, 16, new ItemBuilder(Material.WATCH).name(C.mHead + "Set Time")
				.lore(C.Italics + "*Click to set the current time*").build());
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		new TimeSetUI(player, Plugin).openInventory(player);
	}
}