package xyz.ufactions.permissions.command;

import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.permissions.PermissionsModule;
import xyz.ufactions.permissions.data.PermissionGroup;
import xyz.ufactions.permissions.database.GroupDatabase;

import java.util.HashMap;
import java.util.List;

public class GlobalPermissionCommand extends CommandBase<PermissionsModule> {

	private final String line = C.cAqua + C.Strike + "----------" + C.cWhite + C.Strike + "----------";

	public GlobalPermissionCommand(PermissionsModule module) {
		super(module, "gperms");
	}

	@Override
	public void execute(final Player caller, final String[] args) {
		if (!caller.hasPermission("*")) {
			return;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("groups")) {
				caller.sendMessage(line);
				caller.sendMessage("Groups");
				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						HashMap<String, List<PermissionGroup>> groups = getRepo().getGlobalGroups();
						for (String server : groups.keySet()) {
							caller.sendMessage(C.cAqua + " " + server);
							for (PermissionGroup group : groups.get(server)) {
								caller.sendMessage(
										" - " + C.cGold + F.capitalizeFirstLetter(group.getName().toLowerCase()));
							}
						}
						caller.sendMessage(line);
					}
				});
				return;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("group")) {
				caller.sendMessage(line);
				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						HashMap<String, List<PermissionGroup>> groups = getRepo().getGlobalGroups();

						for (String server : groups.keySet()) {
							for (PermissionGroup pg : groups.get(server)) {
								if (pg.getName().equalsIgnoreCase(args[1])) {
									getRepo().loadGroup(pg, server);
									caller.sendMessage("Information:");
									caller.sendMessage(C.cAqua + server);
									caller.sendMessage(" ");
									caller.sendMessage("Inheritance:");
									for (String child : pg.getInheritance()) {
										caller.sendMessage("* " + C.cGold + child);
									}
									caller.sendMessage(" ");
									caller.sendMessage("Permissions:");
									for (String permission : pg.getPermissions()) {
										caller.sendMessage("* " + C.cGold + permission);
									}
									caller.sendMessage(" ");
									caller.sendMessage("Options:");
									caller.sendMessage("Default = " + pg.isDefault());
									caller.sendMessage("Prefix = " + pg.getPrefix());
									caller.sendMessage("Suffix = " + pg.getSuffix());
								}
							}
						}
						caller.sendMessage(line);
					}
				});
				return;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("group")) {
				final String group = args[1];
				if (args[2].equalsIgnoreCase("server")) {
					UtilPlayer.message(caller, F.main("DATABASE", "Connecting..."));
					Plugin.runAsync(new Runnable() {

						@Override
						public void run() {
							String a = getRepo().groupExist(group);
							if (a == null) {
								UtilPlayer.message(caller,
										F.error(AliasUsed, "Group " + F.elem(group) + C.cRed + " does not exist."));
							} else {
								UtilPlayer.message(caller, F.main(AliasUsed, F.elem(F.capitalizeFirstLetter(group))
										+ " will only load on " + F.elem(a) + "."));
							}
						}
					});
					return;
				}
				if (args[2].equalsIgnoreCase("create")) {
					UtilPlayer.message(caller, F.main("DATABASE", "Connecting..."));
					Plugin.runAsync(new Runnable() {

						@Override
						public void run() {
							String a = getRepo().groupExist(group);
							if (a != null) {
								UtilPlayer.message(caller, F.error(AliasUsed, "Group " + F.elem(group) + C.cRed
										+ " already exists on " + F.elem(a) + C.cRed + "."));
							} else {
								getRepo().createGroup(group, "", "", true);
								UtilPlayer.message(caller, F.main(AliasUsed, "Group " + F.elem(group) + " created."));
							}
						}
					});
					return;
				}
			}
		}
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("group")) {
				final String group = args[1];
				if (args[2].equalsIgnoreCase("server")) {
					final String server = args[3];
					UtilPlayer.message(caller, F.main("DATABASE", "Connecting..."));
					Plugin.runAsync(new Runnable() {

						@Override
						public void run() {
							if (getRepo().groupExist(group) == null) {
								UtilPlayer.message(caller,
										F.error(AliasUsed, "Group " + F.elem(group) + C.cRed + " does not exist."));
							} else {
								getRepo().changeServer(group, server);
								UtilPlayer.message(caller, F.main(AliasUsed, F.elem(group)
										+ "'s loading server has been changed to " + F.elem(server) + "."));
							}
						}
					});
					return;
				}
			}
		}
		// more commands args
		System.out.println(args.length);
		caller.sendMessage(C.cGray + C.Italics
				+ "*Any changes done here are global meaning that every group created or permission added to a group is globally changed. Note: Anything here will be ran in real time meaning that all the tasks will be updated live so don't worry if there is little delay.*");
		caller.sendMessage("Legend:");
		caller.sendMessage(C.cAqua + "Server");
		caller.sendMessage(C.cGold + "Information");
		caller.sendMessage("Commands:");
		caller.sendMessage("/gperms group [group]");
		caller.sendMessage("/gperms group <group> create");
		caller.sendMessage("/gperms group <group> server [server]");
	}

	private GroupDatabase getRepo() {
		return Plugin.getPermissionsManager().getGroupRepository();
	}
}