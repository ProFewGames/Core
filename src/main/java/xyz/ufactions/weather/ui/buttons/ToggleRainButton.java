package xyz.ufactions.weather.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.ItemBuilder;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.weather.WeatherModule;

public class ToggleRainButton extends ShopItem<WeatherModule> {

	public ToggleRainButton(WeatherModule module) {
		super(module, 12, new ItemBuilder(Material.WATER_BUCKET).name(C.mHead + "Toggle Rain")
				.lore(C.Italics + "*Click to toggle rain*").build());
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		boolean storm = player.getWorld().hasStorm();
		if (!storm) {
			if (Plugin.getWeather(player.getWorld()).isDisableRain()) {
				UtilPlayer.message(player,
						F.error(Plugin.getName(), "Unable to toggle rain on because natural rain has been disabled."));
				return;
			}
		}
		player.getWorld().setStorm(!storm);
		UtilPlayer.message(player, F.main(Plugin.getName(), "Toggled rain " + F.oo(!storm) + "."));
	}
}