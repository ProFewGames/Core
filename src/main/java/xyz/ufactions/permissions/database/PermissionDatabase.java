package xyz.ufactions.permissions.database;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.permissions.data.PermissionUser;

public class PermissionDatabase extends RepositoryBase {

	// Permission Nodes
	private final String CREATE_PLAYER_PERMISSION = "CREATE TABLE IF NOT EXISTS `player_permissions` (`player_uuid` varchar(50) NOT NULL, `permission_node` varchar(50) NOT NULL, `server_group` varchar(50) NOT NULL);";
	private final String SELECT_ALL_PERMISSIONS = "SELECT * FROM `player_permissions` WHERE `player_uuid`=?;";
	private final String SELECT_ALL_SERVER_PERMISSIONS = "SELECT * FROM `player_permissions` WHERE `player_uuid`=? AND `server_group`=?;";
	private final String INSERT_PERMISSION_STATEMENT = "INSERT INTO `player_permissions` (`player_uuid`, `permission_node`, `server_group`) VALUES (?, ?, ?);";
	private final String DELETE_PERMISSION_STATEMENT = "DELETE FROM `player_permissions` WHERE `player_uuid` = ? AND `permission_node` = ? AND `server_group`=?;";

	// User groups
	private final String CREATE_PLAYER_GROUPS = "CREATE TABLE IF NOT EXISTS	 `player_groups` (`player_uuid` varchar(50) NOT NULL, `player_group` varchar(50) NOT NULL, `server_group` varchar (50) NOT NULL);";
	private final String SELECT_ALL_GROUPS = "SELECT * FROM `player_groups` WHERE `player_uuid` = ?;";
	private final String INSERT_GROUP_STATEMENT = "INSERT INTO `player_groups` (`player_uuid`, `player_group`, `server_group`) VALUES (?, ?, ?);";
	private final String DELETE_GROUP_STATEMENT = "DELETE FROM `player_groups` WHERE `player_uuid` = ? AND `player_group` = ?;";

	private final String server_group;

	private boolean debug = false;

	public PermissionDatabase(JavaPlugin plugin, String server_name) {
		super(plugin, DBPool.MAIN);

		server_group = server_name;

		debug = new File("perm_debug.dat").exists();
	}

	// XXX Debug

	private void debug(UUID uuid, String string) {
		debug(true, Bukkit.getOfflinePlayer(uuid).getName() + "(" + uuid + ") " + string);
	}

	private void debug(String group, UUID uuid, String string) {
		debug(false, "[" + group + "] " + Bukkit.getOfflinePlayer(uuid).getName() + "(" + uuid + ") " + string);
	}

	private void debug(boolean user, String string) {
		if (debug)
			System.out.println("[PermissionDatabase] " + (user ? "[USER]" : "[GROUP]") + " " + string);
	}

	// XXX Groups

	public void addGroup(UUID uuid, String group, boolean global) {
		group = group.toLowerCase();
		if (!a(getGroups(uuid), group)) {
			executeUpdate(INSERT_GROUP_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
					new ColumnVarChar("permission_group", 50, group),
					new ColumnVarChar("server_group", 50, global ? "global" : server_group));
			debug(group, uuid, "Added to group.");
		} else {
			debug(group, uuid, "Already in group.");
		}
	}

	public void removeGroup(UUID uuid, String group) {
		group = group.toLowerCase();
		executeUpdate(DELETE_GROUP_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnVarChar("permission_group", 50, group));
		debug(group, uuid, "Removed from group.");
	}

	public HashMap<String, List<String>> getGlobalGroups(UUID uuid) {
		final HashMap<String, List<String>> groups = new HashMap<>();

		executeQuery(SELECT_ALL_GROUPS, new ResultSetCallable() {

			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(3);
					if (!groups.containsKey(server))
						groups.put(server, new ArrayList<String>());
					groups.get(server).add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		debug(false, "Global groups returned for `" + uuid + "` map: " + groups);
		return groups;
	}

	public List<String> getGroups(UUID uuid) {
		final List<String> groups = new ArrayList<>();

		executeQuery(SELECT_ALL_GROUPS, new ResultSetCallable() {

			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(3);
					if (server.equalsIgnoreCase(server) || server.equalsIgnoreCase("global"))
						groups.add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		debug(false, "Groups returned for `" + uuid + "` list: " + groups);
		return groups;
	}

	private boolean a(List<String> b, String c) {
		for (String d : b) {
			if (d.equalsIgnoreCase(c)) {
				return true;
			}
		}
		return false;
	}

	public boolean inGroup(UUID uuid, String group) {
		return a(getGroups(uuid), group);
	}

	// XXX Permissions

	public void saveUser(Player player, PermissionUser user) {
		for (String perm : user.getPermissions()) {
			addPermission(player.getUniqueId(), perm);
		}
	}

	public void addPermission(UUID uuid, String permission) {
		permission = permission.toLowerCase();
		if (!a(getPermissions(uuid), permission)) {
			executeUpdate(INSERT_PERMISSION_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
					new ColumnVarChar("permission_node", 50, permission),
					new ColumnVarChar("server_group", 50, server_group));
			debug(uuid, "Added permission `" + permission + "` to server group `" + server_group + "`");
		} else {
			debug(uuid, "Already has the permission `" + permission + "`");
		}
	}

	public void deletePermission(UUID uuid, String permission) {
		permission = permission.toLowerCase();
		executeUpdate(DELETE_PERMISSION_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnVarChar("permission_node", 50, permission),
				new ColumnVarChar("server_group", 50, server_group));
		debug(true, "Deleted permission `" + permission + "` from server group `" + server_group + "`");
	}

	public List<String> getPermissions(UUID uuid) {
		final List<String> permissions = new ArrayList<>();

		executeQuery(SELECT_ALL_SERVER_PERMISSIONS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					permissions.add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()), new ColumnVarChar("server_group", 50, server_group));
		debug(uuid, "Permission list for server `" + server_group + "`: " + permissions);
		return permissions;
	}

	public HashMap<String, List<String>> getGlobalPermissions(UUID uuid) {
		final HashMap<String, List<String>> permissions = new HashMap<>();

		executeQuery(SELECT_ALL_PERMISSIONS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(3);
					if (!permissions.containsKey(server))
						permissions.put(server, new ArrayList<String>());
					permissions.get(server).add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		debug(uuid, "Permission map: " + permissions);
		return permissions;
	}

	// XXX Initialize

	@Override
	protected void initialize() {
		executeUpdate(CREATE_PLAYER_GROUPS);
		executeUpdate(CREATE_PLAYER_PERMISSION);
	}

	@Override
	protected void update() {
	}
}