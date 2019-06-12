package xyz.ufactions.weather.commands;

import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.weather.WeatherModule;
import xyz.ufactions.weather.ui.WeatherUI;

public class WeatherCommand extends CommandBase<WeatherModule> {

	public WeatherCommand(WeatherModule module) {
		super(module, "weather");
	}

	@Override
	public void execute(Player caller, String[] args) {
		String aZ = "core.weather";
		if (!caller.hasPermission(aZ)) {
			UtilPlayer.message(caller, F.noPermission());
			return;
		}
		new WeatherUI(caller, Plugin).openInventory(caller);
	}
}