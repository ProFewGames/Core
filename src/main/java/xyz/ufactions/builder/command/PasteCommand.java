package xyz.ufactions.builder.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class PasteCommand extends CommandBase<BuilderModule> {

	public PasteCommand(BuilderModule plugin) {
		super(plugin, "paste");
	}

	@Override
	public void execute(Player player, String[] args) {
		if(!player.hasPermission("core.command.builder.paste")) {
			player.sendMessage(F.noPermission());
			return;
		}
		if (!Plugin.isBuilderMode(player)) {
			UtilPlayer.message(player,
					F.main(Plugin.getName(), "Cannot execute action as the player is not in builder mode."));
			return;
		}
		if (args == null || args.length == 0) {
			UtilPlayer.message(player, F.main(Plugin.getName(), "Enter a specified rotation amount."));
			return;
		}
		int rotate = 0;
		try {
			rotate = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			UtilPlayer.message(player, F.main(Plugin.getName(), F.elem(args[0]) + " is not a number."));
			return;
		}
		for (int i = 0; i < rotate; i++) {
			Bukkit.dispatchCommand(player, "/rotate 90");
			Bukkit.dispatchCommand(player, "/paste");
		}
		UtilPlayer.message(player, F.main(Plugin.getName(),
				"Successfully pasted your clipboard " + F.elem("360") + " degrees around your player."));
	}
}