
package xyz.ufactions.permissions.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.permissions.PermissionsModule;
import xyz.ufactions.permissions.data.PermissionGroup;

public class PermissionCommand extends CommandBase<PermissionsModule> {

	public PermissionCommand(PermissionsModule module) {
		super(module, "permissions", "permission", "pex", "permissionsex", "megaperms", "mperms");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("*")) {
			if (args.length == 1) {
				return getMatches(args[0], Arrays.asList("user", "group", "reload"));
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("user")) {
					return getMatches(args[1], UtilServer.getPlayerNames());
				}
				if (args[0].equalsIgnoreCase("group")) {
					return getMatches(args[1], Plugin.getPermissionsManager().getGroups());
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("group")) {
					return getMatches(args[2],
							Arrays.asList("create", "delete", "add", "remove", "prefix", "suffix", "child"));
				}
				if (args[0].equalsIgnoreCase("user")) {
					return getMatches(args[2], Arrays.asList("group", "add", "remove"));
				}
			}
			if (args.length == 4) {
				if (args[0].equalsIgnoreCase("group")) {
					if (args[2].equalsIgnoreCase("child")) {
						return getMatches(args[3], Arrays.asList("add", "remove"));
					}
				}
				if (args[0].equalsIgnoreCase("user")) {
					if (args[2].equalsIgnoreCase("group")) {
						return getMatches(args[3], Arrays.asList("set", "add", "remove"));
					}
				}
			}
			if (args.length == 5) {
				if (args[0].equalsIgnoreCase("user")) {
					if (args[2].equalsIgnoreCase("group")) {
						if (args[3].equalsIgnoreCase("remove")) {
							Player player = Bukkit.getPlayer(args[1]);
							if (player != null) {
								return getMatches(args[4],
										Plugin.getPermissionsManager().getGroups(player.getUniqueId()));
							}
						}
						if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("set")) {
							return getMatches(args[4], Plugin.getPermissionsManager().getGroups());
						}
					}
				}
				if (args[0].equalsIgnoreCase("group")) {
					if (args[2].equalsIgnoreCase("child")) {
						if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove")) {
							return getMatches(args[4], Plugin.getPermissionsManager().getGroups());
						}
					}
				}
			}
		}
		return super.onTabComplete(sender, commandLabel, args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final Player player, String[] args) {
		if (!player.hasPermission("*")) {
			return;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("groups") || args[0].equalsIgnoreCase("group")) {
				player.sendMessage(C.cAqua + "Groups:");
				for (String group : Plugin.getPermissionsManager().getGroups()) {
					player.sendMessage(C.cWhite + "- " + C.cGold + group + " " + C.cGray
							+ Plugin.getPermissionsManager().getInheritance(group));
				}
				return;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						Plugin.getPermissionsManager().loadGroups();
						for (Player player : Bukkit.getOnlinePlayers()) {
							Plugin.getPermissionsManager().refresh(player);
						}
						Bukkit.getServer().getScheduler().runTask(Plugin.getPlugin(), new Runnable() {

							@Override
							public void run() {
								player.sendMessage(C.cAqua + "User permissions reloaded");
							}
						});
					}
				});
				return;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("group")) {
				final PermissionGroup group = Plugin.getPermissionsManager().getGroup(args[1]);
				if (group == null) {
					player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
					return;
				}
				player.sendMessage(C.cAqua + C.Strike + "----------" + C.cWhite + C.Strike + "----------");
				player.sendMessage(C.cWhite + "Permissions Information:");
				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						player.sendMessage(C.cWhite + "Inheritance:");
						for (String inheritance : group.getInheritance()) {
							player.sendMessage(C.cWhite + "* " + C.cGold + inheritance);
						}
						player.sendMessage("");
						player.sendMessage(C.cWhite + "Permissions:");
						for (String perm : group.getPermissions()) {
							player.sendMessage(C.cWhite + "* " + C.cGold + perm);
						}
						player.sendMessage("");
						player.sendMessage(C.cWhite + "Options:");
						player.sendMessage(C.cWhite + "Default = " + group.isDefault());
						player.sendMessage(C.cWhite + "Prefix = \"" + group.getPrefix() + "\"");
						player.sendMessage(C.cWhite + "Suffix = \"" + group.getSuffix() + "\"");
						player.sendMessage(C.cAqua + C.Strike + "----------" + C.cWhite + C.Strike + "----------");
					}
				});
				return;
			}
			if (args[0].equalsIgnoreCase("user")) {
				final UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
				if (uuid == null) {
					player.sendMessage(C.cRed + "Player " + args[1] + " has never been online before.");
					return;
				}
				player.sendMessage(C.cGray + C.Italics + "*Showing user information using the database in real time.*");
				player.sendMessage(C.cAqua + C.Strike + "----------" + C.cWhite + C.Strike + "----------");
				player.sendMessage(C.cWhite + "Permissions Information:");
				Plugin.runAsync(new Runnable() {

					public void run() {
						player.sendMessage(" ");
						player.sendMessage(C.cWhite + "Groups:");
						HashMap<String, List<String>> groups = Plugin.getPermissionsManager().getRepository()
								.getGlobalGroups(uuid);
						for (String server : groups.keySet()) {
							player.sendMessage(C.cAqua + server);
							for (String group : groups.get(server)) {
								player.sendMessage(C.cWhite + "- " + C.cGold + group);
							}
						}
						player.sendMessage(" ");
						player.sendMessage(C.cWhite + "Permissions:");
						HashMap<String, List<String>> permissions = Plugin.getPermissionsManager().getRepository()
								.getGlobalPermissions(uuid);
						for (String server : permissions.keySet()) {
							player.sendMessage(C.cAqua + server);
							for (String permission : permissions.get(server)) {
								player.sendMessage(C.cWhite + "* " + C.cGold + permission);
							}
						}
						player.sendMessage(C.cAqua + C.Strike + "----------" + C.cWhite + C.Strike + "----------");
					}
				});
				return;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("group")) {
				PermissionGroup group = Plugin.getPermissionsManager().getGroup(args[1]);
				if (args[2].equalsIgnoreCase("delete")) {
					if (group == null) {
						player.sendMessage(C.cRed + "This group does not exist.");
						return;
					}
					Plugin.getPermissionsManager().deleteGroup(group.getName());
					player.sendMessage(C.cAqua + "Deleted group " + args[1] + ".");
					return;
				}
				if (args[2].equalsIgnoreCase("create")) {
					if (group != null) {
						player.sendMessage(C.cRed + "This group already exists.");
						return;
					}
					Plugin.getPermissionsManager().createGroup(args[1]);
					player.sendMessage(C.cAqua + "Created group " + args[1] + ".");
					return;
				}
				if (args[2].equalsIgnoreCase("prefix")) {
					if (group == null) {
						player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
						return;
					}
					player.sendMessage(group.getName() + "'s prefix is \"" + group.getPrefix() + "\"");
					return;
				}
				if (args[2].equalsIgnoreCase("suffix")) {
					if (group == null) {
						player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
						return;
					}
					player.sendMessage(group.getName() + "'s suffix is \"" + group.getSuffix() + "\"");
					return;
				}
				if (args[2].equalsIgnoreCase("child")) {
					if (group == null) {
						player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
						return;
					}
					player.sendMessage(C.cAqua + group.getName() + "'s children:");
					for (String child : group.getInheritance()) {
						player.sendMessage(C.cWhite + "- " + C.cGold + child);
					}
					return;
				}
			}
		}
		if (args.length == 4) {
			if (args[0].equalsIgnoreCase("group")) {
				PermissionGroup group = Plugin.getPermissionsManager().getGroup(args[1]);
				if (group == null) {
					player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
					return;
				}
				if (args[2].equalsIgnoreCase("prefix")) {
					String prefix = args[3];
					if (prefix.equals("\"\"")) {
						prefix = "";
					}
					Plugin.getPermissionsManager().setPrefix(group, prefix);
					player.sendMessage(C.cAqua + group.getName() + "'s prefix set to \"" + prefix + "\"");
					return;
				}
				if (args[2].equalsIgnoreCase("suffix")) {
					String suffix = args[3];
					if (suffix.equals("\"\"")) {
						suffix = "";
					}
					Plugin.getPermissionsManager().setSuffix(group, suffix);
					player.sendMessage(C.cAqua + group.getName() + "'s suffix set to \"" + suffix + "\"");
					return;
				}
				if (args[2].equalsIgnoreCase("add")) {
					String permission = args[3];
					Plugin.getPermissionsManager().addPermission(group.getName(), permission);
					player.sendMessage(C.cAqua + permission + " added to the group " + group.getName());
					return;
				}
				if (args[2].equalsIgnoreCase("remove")) {
					String permission = args[3];
					Plugin.getPermissionsManager().removePermission(group.getName(), permission);
					player.sendMessage(C.cAqua + permission + " removed from the group " + group.getName());
					return;
				}
			}
			if (args[0].equalsIgnoreCase("user")) {
				UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
				if (uuid == null) {
					player.sendMessage(C.cRed + "Player " + args[1] + " has never been online before.");
					return;
				}
				String permission = args[3];
				String name = Bukkit.getOfflinePlayer(args[1]) != null ? Bukkit.getOfflinePlayer(args[1]).getName()
						: args[1];
				if (args[2].equalsIgnoreCase("add")) {
					Plugin.getPermissionsManager().addPermission(uuid, permission);
					player.sendMessage(C.cAqua + name + " now has the permission " + permission + ".");
					return;
				}
				if (args[2].equalsIgnoreCase("remove")) {
					Plugin.getPermissionsManager().removePermission(uuid, permission);
					player.sendMessage(C.cAqua + permission + " removed from " + name + ".");
					return;
				}
			}
		}
		if (args.length == 5) {
			if (args[0].equalsIgnoreCase("group")) {
				PermissionGroup group = Plugin.getPermissionsManager().getGroup(args[1]);
				if (group == null) {
					player.sendMessage(C.cRed + "Group " + args[1] + " does not exist.");
					return;
				}
				if (args[2].equalsIgnoreCase("child")) {
					PermissionGroup child = Plugin.getPermissionsManager().getGroup(args[4]);
					if (child == null) {
						player.sendMessage(C.cRed + "Group " + args[4] + " does not exist.");
						return;
					}
					if (args[3].equalsIgnoreCase("add")) {
						Plugin.getPermissionsManager().addChild(group.getName(), child.getName());
						player.sendMessage(C.cAqua + child.getName() + " is now a child of " + group.getName() + ".");
						return;
					}
					if (args[3].equalsIgnoreCase("remove")) {
						Plugin.getPermissionsManager().removeChild(group.getName(), child.getName());
						player.sendMessage(
								C.cRed + child.getName() + " is no longer a child of " + group.getName() + ".");
						return;
					}
				}
			}
			if (args[0].equalsIgnoreCase("user")) {
				UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
				if (uuid == null) {
					player.sendMessage(C.cRed + "Player " + args[1] + " has never been online before.");
					return;
				}
				String name = uuid != null ? Bukkit.getOfflinePlayer(args[1]).getName() : args[1];
				if (args[2].equalsIgnoreCase("group")) {
					String group = args[4];
					if (args[3].equalsIgnoreCase("set")) {
						Plugin.getPermissionsManager().setGroup(uuid, group);
						player.sendMessage(C.cAqua + name + "'s group has been set as " + group + ".");
						return;
					}
					if (args[3].equalsIgnoreCase("add")) {
						Plugin.getPermissionsManager().addGroup(uuid, group);
						player.sendMessage(C.cAqua + name + " has been added to the group " + group + ".");
						return;
					}
					if (args[3].equalsIgnoreCase("remove")) {
						Plugin.getPermissionsManager().removeGroup(uuid, group);
						player.sendMessage(C.cAqua + name + " has been removed from the group " + group + ".");
						return;
					}
				}
			}
		}
		player.sendMessage(C.cWhite + "");
		player.sendMessage(C.cWhite + "Commands:");
		player.sendMessage(C.cWhite + "/permissions reload");
		player.sendMessage(C.cWhite + "/permissions group [group]");
		player.sendMessage(C.cWhite + "/permissions group <group> create");
		player.sendMessage(C.cWhite + "/permissions group <group> delete");
		player.sendMessage(C.cWhite + "/permissions group <group> add <permission>");
		player.sendMessage(C.cWhite + "/permissions group <group> remove <permission>");
		player.sendMessage(C.cWhite + "/permissions group <group> prefix [prefix]");
		player.sendMessage(C.cWhite + "/permissions group <group> suffix [suffix]");
		player.sendMessage(C.cWhite + "/permissions group <group> child [add|remove] [group]");
		player.sendMessage(C.cWhite + "/permissions user <player>");
		player.sendMessage(C.cWhite + "/permissions user <player> group set <group>");
		player.sendMessage(C.cWhite + "/permissions user <player> group add <group>");
		player.sendMessage(C.cWhite + "/permissions user <player> group remove <group>");
		player.sendMessage(C.cWhite + "/permissions user <player> add <permission>");
		player.sendMessage(C.cWhite + "/permissions user <player> remove <permission>");
		player.sendMessage(C.cGray + C.Italics
				+ "*Note: any user adding/removing permission defaults to the server you're currently on. To execute these actions for a specific server, you must be on that server then execute the command.*");
		return;
	}
}