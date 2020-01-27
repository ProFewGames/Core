package xyz.ufactions.permissions.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.permissions.PermissionsModule;
import xyz.ufactions.permissions.data.PermissionGroup;

public class RankCommand extends CommandBase<PermissionsModule> {

	public RankCommand(PermissionsModule module) {
		super(module, "rank");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
			if (args.length == 1) {
				return getMatches(args[0], UtilServer.getPlayerNames());
		}
		return super.onTabComplete(sender, commandLabel, args);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length == 0) {
			UtilPlayer.message(player, F.help("/rank <player>", "Display the player's rank"));
			return;
		} else {
			Player target = UtilPlayer.searchOnline(player, args[0], true);
			if (target == null) {
				return;
			} else {
				PermissionGroup group = null;

				try {
					group = Plugin.getPermissionsManager()
							.getGroup(Plugin.getPermissionsManager().getGroups(target.getUniqueId()).get(0));
				} catch (IndexOutOfBoundsException ex) {
					group = Plugin.getPermissionsManager().getDefGroup();
				}
				if (group == null) {
					group = new PermissionGroup("null");
				}
				UtilPlayer.message(player, F.main(Plugin.getName(), target.getName() + "'s rank is "
						+ ChatColor.translateAlternateColorCodes('&', group.getPrefix())));
			}
		}
	}
}