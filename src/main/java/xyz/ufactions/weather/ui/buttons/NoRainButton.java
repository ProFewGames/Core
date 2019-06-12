package xyz.ufactions.weather.ui.buttons;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.shop.ShopItem;
import xyz.ufactions.weather.WeatherModule;
import xyz.ufactions.weather.WeatherOptions;

public class NoRainButton extends ShopItem<WeatherModule> {

	public NoRainButton(Player player, WeatherModule module) {
		super(module, 10, a(player, module));
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		WeatherOptions weather = Plugin.getWeather(player.getWorld());
		weather.setDisableRain(!weather.isDisableRain());
		UtilPlayer.message(player,
				F.main(Plugin.getName(), "Natural rain has been turned " + F.oo(!weather.isDisableRain()) + "."));
	}

	private static ItemStack a(Player player, WeatherModule module) {
		WeatherOptions weather = module.getWeather(player.getWorld());
		boolean a = weather.isDisableRain();
		ItemStack item = new ItemStack(Material.WATER_BUCKET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName((a ? C.cGreen + "Enable" : C.cRed + "Disable") + " Natural Rain");
		meta.setLore(Arrays.asList(C.cGray + C.Italics + "*Click to enable/disable natural rain*"));
		item.setItemMeta(meta);
		return item;
	}
}