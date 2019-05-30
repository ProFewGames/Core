package xyz.ufactions.tags.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class TitleCreateCommand extends CommandBase<TitleModule> {

	public TitleCreateCommand(TitleModule module) {
		super(module, "titlecreate", "createtitle");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (Plugin.getTagManager().getTokens(player) <= 0) {
			UtilPlayer.message(player, F.main(Plugin.getName(), C.cRed + "    You don't have any " + C.cRed + C.Line
					+ "/titlecreate" + ChatColor.RESET + C.cRed + " credits!"));
			UtilPlayer.message(player, F.main(Plugin.getName(),
					C.cGold + "  You can buy more at " + C.cYellow + C.Line + "buy.ufactions.xyz"));
			return;
		} else {
			if (args.length != 1) {
				UtilPlayer.message(player, F.help("/" + AliasUsed + " <title>", "Create a brand new title :D "
						+ C.Italics + C.cGray + "*Colors are added in via /titlecolor not when you create a title*"));
				return;
			} else {
				if (args[0].length() < 3) {
					UtilPlayer.message(player, F.main(Plugin.getName(), "Your title length must be greater than 3."));
					return;
				} else {
					String title = args[0];
					title = title.toLowerCase();
					if (Plugin.getTagManager().hasTag(player, title)) {
						UtilPlayer.message(player, F.main(Plugin.getName(), "You already own this tag!"));
						return;
					} else {
						Plugin.getTagManager().createCustomTag(player, title);
						UtilPlayer.message(player,
								F.main(Plugin.getName(), "You have created the title: " + C.cWhite + title));
						Plugin.getTagManager().updateTokens(player.getUniqueId(), -1);
						Plugin.waitForResponse(player, player.getUniqueId(), C.cWhite + C.Bold + "You now have "
								+ C.cGold + C.Bold + "%amount%" + C.cWhite + C.Bold + " titlecreate credits.");
						return;
					}
				}
			}
		}
	}
}