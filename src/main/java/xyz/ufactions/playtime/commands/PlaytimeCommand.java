package xyz.ufactions.playtime.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.Callback;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.playtime.PlaytimeModule;

public class PlaytimeCommand extends CommandBase<PlaytimeModule> {

	public PlaytimeCommand(PlaytimeModule module) {
		super(module, "playtime");
	}

	@Override
	public void execute(final Player caller, String[] args) {
		if (args.length == 1) {
			String lookingFor = args[0];
			UtilPlayer.searchOffline(b(lookingFor), new Callback<String>() {

				@SuppressWarnings("deprecation")
				@Override
				public void run(String name) {
					if (name != null) {
						a(caller, Bukkit.getOfflinePlayer(name));
					}
				}
			}, caller, lookingFor, true);
			return;
		}
		a(caller, caller);
	}

	private void a(Player a, OfflinePlayer b) {
		String c;
		if (a.getUniqueId() != b.getUniqueId()) {
			c = F.elem(b.getName()) + "'s";
		} else {
			c = "Your";
		}
		UtilPlayer.message(a, F.main(Plugin.getName(), c + " total playtime is: "
				+ F.elem(Plugin.getDurationBreakdown(Plugin.getUser(b).getPlayTime()) + ".")));
	}

	private List<String> b(String a) {
		List<String> b = new ArrayList<>();
		for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (b.contains(player.getName()))
				continue;
			if (player.getName().toLowerCase().startsWith(a.toLowerCase())) {
				b.add(player.getName());
			}
		}
		return b;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 1) {
			return b(args[0]);
		}
		return super.onTabComplete(sender, commandLabel, args);
	}
}