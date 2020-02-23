package xyz.ufactions.tablist.commands;

import java.util.HashMap;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.tablist.Tablist;

public class TablistCommand extends CommandBase<Tablist> {

	public TablistCommand(Tablist module) {
		super(module, "tablist");
	}

	@Override
	public void execute(Player caller, String[] args) {
		if (caller.hasPermission("command.tablist")) {
			if (args.length == 0) {
				UtilPlayer.message(caller, F.main(Plugin.getName(), "list:"));
				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						HashMap<String, String> tablist = Plugin.getRepository().getTablist();
						for (String group : tablist.keySet()) {
							UtilPlayer.message(caller, F.elem(group) + " "
									+ ChatColor.translateAlternateColorCodes('&', tablist.get(group)));
						}
					}
				});
				return;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					Plugin.reload();
					UtilPlayer.message(caller, F.main(Plugin.getName(), "Tablists' reloaded."));
					return;
				}
			}
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("delete")) {
					String group = args[0];
					UtilPlayer.message(caller, F.main(Plugin.getName(), "Deleting " + group + "'s tablist"));
					Plugin.runAsync(new Runnable() {

						@Override
						public void run() {
							Plugin.getRepository().deleteTablist(group);
							UtilPlayer.message(caller,
									F.main(Plugin.getName(), group + "'s tablist successfully deleted!"));
						}
					});
					return;
				}
			}
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("set")) {
					String group = args[0];
					String prefix = args[2];
					Plugin.runAsync(new Runnable() {

						@Override
						public void run() {
							HashMap<String, String> tablist = Plugin.getRepository().getTablist();
							for (String a : tablist.keySet()) {
								if (group.equalsIgnoreCase(a)) {
									Plugin.getRepository().updateTablist(group, prefix);
									UtilPlayer.message(caller,
											F.main(Plugin.getName(), group + "'s tablist updated as " + prefix + "."));
									return;
								}
							}
							Plugin.getRepository().createTablist(group, prefix);
							UtilPlayer.message(caller,
									F.main(Plugin.getName(), group + "'s tablist inserted as " + prefix + "."));
						}
					});
					return;
				}
			}
			UtilPlayer.message(caller, F.help("/tablist", "View the current tablists"));
			UtilPlayer.message(caller, F.help("/tablist reload", "Reload everyone on the server's tablist"));
			UtilPlayer.message(caller, F.help("/tablist <group> delete", "Delete <group>'s tablist"));
			UtilPlayer.message(caller,
					F.help("/tablist <group> set <tablist>", "Set <tablist> as the tablist for <group>"));
		}
	}
}