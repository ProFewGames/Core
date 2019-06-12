package xyz.ufactions.builder.command;

import org.bukkit.entity.Player;

import xyz.ufactions.builder.BuilderModule;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class ToggleCommand extends CommandBase<BuilderModule> {

	public ToggleCommand(BuilderModule module) {
		super(module, "toggle");
	}

	@Override
	public void execute(Player player, String[] args) {
		if(!player.hasPermission("core.builder.toggle")) {
			player.sendMessage(F.noPermission());
			return;
		}
		boolean toggle = Plugin.toggleBuilderMode(player);
		UtilPlayer.message(player, F.main(Plugin.getName(), "You toggled your builder mode " + F.oo(toggle) + "."));
	}
}