package xyz.ufactions.builder.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.commands.MultiCommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class BuilderCommand extends MultiCommandBase<BuilderModule> {

	public BuilderCommand(BuilderModule module) {
		super(module, "builder");

		addCommand(new PasteCommand(module));
		addCommand(new ToggleCommand(module));
	}

	@Override
	protected void help(Player player, String[] args) {
		if (!player.hasPermission("core.command.builder")) {
			player.sendMessage(F.noPermission());
			return;
		}
		UtilPlayer.message(player, F.help("/builder toggle", "Toggles your builder mode."));
		UtilPlayer.message(player,
				F.help("/builder paste <amount>", "Paste whatever is in your clipboard <amount> degrees around you."));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("core.builder")) {
			if (args.length == 1) {
				return getMatches(args[0], Arrays.asList("toggle", "paste"));
			}
		}
		return null;
	}
}