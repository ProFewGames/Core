package xyz.ufactions.scoreboard.commands;

import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.scoreboard.ScoreboardModule;

public class ScoreboardCommand extends CommandBase<ScoreboardModule> {

	public ScoreboardCommand(ScoreboardModule module) {
		super(module, "sb", "board");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (!Plugin.TogglableScoreboard) {
			UtilPlayer.message(player, F.error(Plugin.getName(), "You cannot toggle your scoreboard in this server."));
		} else {
			UtilPlayer.message(player, F.main(Plugin.getName(),
					"You have toggled your scoreboard " + F.oo(!Plugin.toggleScoreboard(player)) + "."));
		}
	}
}