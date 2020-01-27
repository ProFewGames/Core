package xyz.ufactions.permissions.data;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.permission.Permission;
import xyz.ufactions.permissions.PermissionsModule;

public class PermissionVault extends Permission {

	private PermissionsModule ap;

	public PermissionVault(Plugin plugin, PermissionsModule Plugin) {
		this.plugin = plugin;

		ap = Plugin;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getName() {
		return "Arkham Permissions";
	}

	@SuppressWarnings("deprecation")
	public OfflinePlayer getPlayer(String player) {
		return Bukkit.getOfflinePlayer(player);
	}

	public boolean playerInGroup(String world, OfflinePlayer player, String group) {
		return ap.getPermissionsManager().inGroup(player, group);
	}

	public boolean playerInGroup(String world, String player, String group) {
		return playerInGroup(world, getPlayer(player), group);
	}

	public boolean playerAddGroup(String world, OfflinePlayer player, String group) {
		ap.getPermissionsManager().addGroup(player.getUniqueId(), group);
		return true;
	}

	public boolean playerAddGroup(String world, String player, String group) {
		return playerAddGroup(world, getPlayer(player), group);
	}

	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group) {
		ap.getPermissionsManager().removeGroup(player.getUniqueId(), group);
		return true;
	}

	public boolean playerRemoveGroup(String world, String player, String group) {
		return playerRemoveGroup(world, getPlayer(player), group);
	}

	public boolean playerAdd(String world, OfflinePlayer player, String permission) {
		ap.getPermissionsManager().addPermission(player.getUniqueId(), permission);
		return true;
	}

	public boolean playerAdd(String world, String player, String permission) {
		return playerAdd(world, getPlayer(player), permission);
	}

	public boolean playerRemove(String world, OfflinePlayer player, String permission) {
		ap.getPermissionsManager().removePermission(player.getUniqueId(), permission);
		return true;
	}

	public boolean playerRemove(String world, String player, String permission) {
		return playerRemove(world, getPlayer(player), permission);
	}

	public boolean groupAdd(String world, String group, String permission) {
		ap.getPermissionsManager().addPermission(group, permission);
		return true;
	}

	public boolean groupRemove(String world, String group, String permission) {
		ap.getPermissionsManager().removePermission(group, permission);
		return true;
	}

	public boolean groupHas(String world, String group, String permission) {
		return ap.getPermissionsManager().hasPermission(group, permission);
	}

	public String[] getPlayerGroups(String world, OfflinePlayer player) {
		List<String> g = ap.getPermissionsManager().getGroups(player.getUniqueId());
		String[] groups = new String[g.size()];
		for (int i = 0; i < g.size(); i++) {
			groups[i] = g.get(i);
		}
		return groups;
	}

	public String[] getPlayerGroups(String world, String player) {
		return getPlayerGroups(world, getPlayer(player));
	}

	public String getPrimaryGroup(String world, OfflinePlayer player) {
		List<String> groups = ap.getPermissionsManager().getGroups(player.getUniqueId());
		if (groups.isEmpty()) {
			return ap.getPermissionsManager().getDefaultGroup();
		} else {
			return groups.get(0);
		}
	}

	public String getPrimaryGroup(String world, String player) {
		return getPrimaryGroup(world, getPlayer(player));
	}

	public boolean playerHas(String world, OfflinePlayer player, String permission) {
		return ap.getPermissionsManager().hasPermission(player, permission);
	}

	public boolean playerHas(String world, String player, String permission) {
		return playerHas(world, getPlayer(player), permission);
	}

	public boolean playerAddTransient(String world, String player, String permission) {
		throw new UnsupportedOperationException();
	}

	public boolean playerAddTransient(String player, String permission) {
		return playerAddTransient(null, getPlayer(player), permission);
	}

	public boolean playerAddTransient(Player player, String permission) {
		return playerAddTransient(null, player, permission);
	}

	public boolean playerRemoveTransient(String world, String player, String permission) {
		throw new UnsupportedOperationException();
	}

	public boolean playerRemoveTransient(String player, String permission) {
		throw new UnsupportedOperationException();
	}

	public boolean playerRemoveTransient(Player player, String permission) {
		throw new UnsupportedOperationException();
	}

	public String[] getGroups() {
		List<String> array = ap.getPermissionsManager().getGroups();
		String[] groups = new String[array.size()];
		for (int i = 0; i < array.size(); i++) {
			groups[i] = array.get(i);
		}
		return groups;
	}

	public boolean hasSuperPermsCompat() {
		return false;
	}

	public boolean hasGroupSupport() {
		return true;
	}
}