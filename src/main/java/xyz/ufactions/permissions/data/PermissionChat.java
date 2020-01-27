package xyz.ufactions.permissions.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import xyz.ufactions.permissions.PermissionsModule;

public class PermissionChat extends Chat {

	private PermissionsModule plugin;

	public PermissionChat(Permission perms, PermissionsModule plugin) {
		super(perms);

		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	private OfflinePlayer getPlayer(String player) {
		return Bukkit.getOfflinePlayer(player);
	}

	@Override
	public String getName() {
		return "Arkham Permissions";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		return plugin.getPermissionsManager().buildPrefix(getPlayer(player));
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		return plugin.getPermissionsManager().buildSuffix(getPlayer(player));
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		return plugin.getPermissionsManager().getPrefix(group);
	}

	@Override
	public void setGroupPrefix(String world, String player, String prefix) {
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		return plugin.getPermissionsManager().getSuffix(group);
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
	}

	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information integers!"));
	}

	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information integers!"));
	}

	public int getGroupInfoInteger(String world, String player, String node, int defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information integer!"));
	}

	public void setGroupInfoInteger(String world, String player, String node, int value) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information integer!"));
	}

	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information doubles!"));
	}

	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information doubles!"));
	}

	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information doubles!"));
	}

	public void setGroupInfoDouble(String world, String group, String node, double value) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information doubles!"));
	}

	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information booleans!"));
	}

	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information booleans!"));
	}

	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information booleans!"));
	}

	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information booleans!"));
	}

	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information strings!"));
	}

	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw (new UnsupportedOperationException("PermissionsG does not support player information strings!"));
	}

	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information strings!"));
	}

	public void setGroupInfoString(String world, String group, String node, String value) {
		throw (new UnsupportedOperationException("PermissionsG does not support group information strings!"));
	}
}