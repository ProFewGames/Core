package xyz.ufactions.permissions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class MPC {

	private static MPC instance;

	public static MPC getInstance() {
		return instance;
	}

	private PermissionsModule module;

	public MPC(PermissionsModule module) {
		this.module = module;

		instance = this;
	}

	public List<String> getMegaPermissions() {
		List<String> perms = new ArrayList<>();
		for (Permission perm : Bukkit.getPluginManager().getPermissions()) {
			String pperm = perm.getName();
			if (pperm.startsWith("mega.")) {
				perms.add(pperm);
			}
		}
		return perms;
	}

	public boolean checkPermission(Player player, String permission) {
		return checkPermission(player, permission, true);
	}

	public boolean checkPermission(Player player, final String permission, boolean notify) {
		module.runAsync(new Runnable() {

			@Override
			public void run() {
				for (String perm : getMegaPermissions()) {
					if (perm.equalsIgnoreCase(permission)) {
						return;
					}
				}
				registerPermission(permission, PermissionDefault.OP);
			}
		});
		boolean hasPerm = player.hasPermission("mega." + permission);
		if (notify && !hasPerm) {
			UtilPlayer.message(player, F.error("Permissions", "You do not have access to this command."));
		}
		return hasPerm;
	}

	public void registerPermission(String permission, PermissionDefault pd) {
		try {
			Bukkit.getPluginManager()
					.addPermission(new Permission("mega." + permission, "Registered permission from MPC", pd));
		} catch (Exception e) {
		}
	}

	public void unregisterPermission(String permission) {
		Bukkit.getPluginManager().removePermission("mega." + permission);
	}
}