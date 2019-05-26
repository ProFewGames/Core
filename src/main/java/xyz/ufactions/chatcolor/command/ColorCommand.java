package xyz.ufactions.chatcolor.command;

import org.bukkit.entity.Player;
import xyz.ufactions.chatcolor.ColorModule;
import xyz.ufactions.chatcolor.ui.ColorUI;
import xyz.ufactions.commands.CommandBase;

public class ColorCommand extends CommandBase<ColorModule> {

	public ColorCommand(ColorModule module) {
		super(module, "color");
	}

	@Override
	public void execute(Player player, String[] args) {
		new ColorUI(player, Plugin);
	}
}