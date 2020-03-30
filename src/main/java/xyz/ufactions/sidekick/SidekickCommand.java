package xyz.ufactions.sidekick;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.sidekick.ui.SidekickUI;

import java.util.Arrays;
import java.util.List;

public class SidekickCommand extends CommandBase<SidekickModule> {

	public SidekickCommand(SidekickModule module) {
		super(module, "pet", "sidekick");
	}

	private boolean check(Player caller) {
		if (!Plugin.hasSidekick(caller)) {
			UtilPlayer.message(caller, F.main(Plugin.getName(), "You do not have an active sidekick."));
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if(args.length == 1) {
			return getMatches(args[0], Arrays.asList("call", "remove", "name"));
		}
		return super.onTabComplete(sender, commandLabel, args);
	}

	@Override
	public void execute(Player caller, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("call")) {
				if (check(caller)) {
					Plugin.getSidekick(caller).teleportToOwner();
					UtilPlayer.message(caller, F.main(Plugin.getName(), "Your sidekick has teleported by your side."));
					return;
				}
			}
			if (args[0].equalsIgnoreCase("remove")) {
				if (check(caller)) {
					Plugin.getSidekick(caller).removePet(true);
					UtilPlayer.message(caller, F.main(Plugin.getName(), "Your sidekick runs away."));
					return;
				}
			}
		}
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("name")) {
				if (check(caller)) {
					String name = "";
					for (int i = 1; i < args.length; i++) {
						name += args[i] + " ";
					}
					name = name.trim();
					Plugin.getSidekick(caller).setPetName(name);
					UtilPlayer.message(caller, F.main(Plugin.getName(), "You've set your sidekick's name to "
							+ F.elem(ChatColor.translateAlternateColorCodes('&', name)) + "."));
					return;
				}
			}
		}
		new SidekickUI(caller, Plugin);
	}
}