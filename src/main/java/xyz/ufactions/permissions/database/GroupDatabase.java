package xyz.ufactions.permissions.database;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.column.ColumnBoolean;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.permissions.data.PermissionGroup;

public class GroupDatabase extends RepositoryBase {

	// XXX Group Table
	private final String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS `groups` (`id` INT AUTO_INCREMENT NOT NULL, `server` varchar(50) NOT NULL, `group` varchar(50) NOT NULL, `prefix` varchar(100), `suffix` varchar(100), `default` boolean, PRIMARY KEY(id));";
	private final String SELECT_GROUPS = "SELECT * FROM `groups`;";
	private final String CREATE_GROUP = "INSERT INTO `groups` (`server`, `group`, `prefix`, `suffix`, `default`) VALUES (?, ?, ?, ?, ?);";
	private final String DELETE_GROUP = "DELETE FROM `groups` WHERE `group`=? AND `server`=?;";
	private final String UPDATE_PREFIX = "UPDATE `groups` SET `prefix`=? WHERE `group`=?;";
	private final String UPDATE_SUFFIX = "UPDATE `groups` SET `suffix`=? WHERE `server`=? AND `group`=?;";
	private final String UPDATE_SERVER = "UPDATE `groups` SET `server`=? WHERE `group`=?;";

	// XXX Group Permissions Table
	private final String CREATE_PERMISSIONS_TABLE = "CREATE TABLE IF NOT EXISTS `group_permissions` (`id` INT AUTO_INCREMENT NOT NULL, `server` varchar(50) NOT NULL, `group` varchar(50) NOT NULL, `permission` varchar(100) NOT NULL, PRIMARY KEY(id));";
	private final String SELECT_PERMISSIONS = "SELECT * FROM `group_permissions` WHERE `group`=?;";
	private final String INSERT_PERMISSION = "INSERT INTO `group_permissions` (`server`, `group`, `permission`) VALUES (?, ?, ?);";
	private final String DELETE_PERMISSION = "DELETE FROM `group_permissions` WHERE `server`=? AND `group`=? AND `permission`=?;";

	// XXX Group Inheritance Table
	private final String CREATE_INHERITANCE_TABLE = "CREATE TABLE IF NOT EXISTS `group_inheritance` (`id` INT AUTO_INCREMENT NOT NULL, `server` varchar(50) NOT NULL, `parent` varchar(50) NOT NULL, `child` varchar(50) NOT NULL, PRIMARY KEY(id));";
	private final String SELECT_INHERITANCE = "SELECT * FROM `group_inheritance` WHERE `parent`=?;";
	private final String INSERT_INHERITANCE = "INSERT INTO `group_inheritance` (`server`, `parent`, `child`) VALUES (?, ?, ?);";
	private final String DELETE_INHERITANCE = "DELETE FROM `group_inheritance` WHERE `server`=? AND `parent`=? AND `child`=?;";
	private final String DELETE_ALL_INHERITANCE = "DELETE FROM `group_inheritance` WHERE `server`=? AND `parent`=?;";

	private final String server;

	private boolean debug;

	public GroupDatabase(JavaPlugin plugin, String server_name) {
		super(plugin, DBPool.MAIN);

		this.server = server_name;

		debug = new File("perm_debug.dat").exists();
	}

	// private final char[] a = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
	// 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
	// 's', 't', 'u', 'v', 'q', 'x', 'y', 'z' };

	public void debug(String string) {
		if (debug)
			System.out.println("[GroupDatabase] " + string);
	}

	// XXX Group booting

	public List<PermissionGroup> loadGroups() {
		List<PermissionGroup> groups = getGroups();
		for (PermissionGroup group : groups) {
			loadGroup(group);
		}
		debug("Successfully loaded up groups. List " + groups);
		return groups;
	}

	public void loadGroup(PermissionGroup group) {
		loadGroup(group, server, true);
	}

	public void loadGroup(PermissionGroup group, String server) {
		loadGroup(group, server, false);
	}

	public void loadGroup(PermissionGroup group, String server, boolean global) {
		debug(" ");
		debug("Pre-Group " + group);
		debug(" ");
		group.setPermissions(getPermissions(group.getName(), server, global));
		group.setInheritance(getInheritance(group.getName(), server, global));
		debug(" ");
		debug("Post-Group " + group);
		debug(" ");
	}

	// XXX Inheritance

	public List<String> getInheritance(String parent) {
		return getInheritance(parent, server, true);
	}

	public List<String> getInheritance(String parent, String server) {
		return getInheritance(parent, server, false);
	}

	public List<String> getInheritance(String parent, final String paramServer, final boolean global) {
		parent = parent.toLowerCase();
		final List<String> children = new ArrayList<>();
		executeQuery(SELECT_INHERITANCE, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(2);
					if (server.equalsIgnoreCase(paramServer) || (server.equalsIgnoreCase("global") && global)) {
						children.add(resultSet.getString(4));
					}
				}
			}
		}, new ColumnVarChar("parent", 50, parent));
		debug("Fetched the list of children for `" + parent + "` on server `" + (global ? "GLOBAL|" : "") + paramServer
				+ "`. List: " + children);
		return children;
	}

	public void addChild(String parent, String child) {
		parent = parent.toLowerCase();
		child = child.toLowerCase();
		executeUpdate(INSERT_INHERITANCE, new ColumnVarChar("server", 50, server),
				new ColumnVarChar("parent", 50, parent), new ColumnVarChar("child", 50, child));
		debug("Added `" + child + "` as a child of `" + parent + "`");
	}

	public void removeChild(String parent, String child) {
		parent = parent.toLowerCase();
		child = child.toLowerCase();
		executeUpdate(DELETE_INHERITANCE, new ColumnVarChar("server", 50, server),
				new ColumnVarChar("parent", 50, parent), new ColumnVarChar("child", 50, child));
		debug("`" + child + "` is no longer a child of `" + parent + "`");
	}

	public void clearChildren(String parent) {
		parent = parent.toLowerCase();
		executeUpdate(DELETE_ALL_INHERITANCE, new ColumnVarChar("server", 50, server),
				new ColumnVarChar("parent", 50, parent));
		debug("All of `" + parent + "`'s children");
	}

	// XXX Permissions

	public List<String> getPermissions(String group) {
		return getPermissions(group, server, true);
	}

	public List<String> getPermissions(String group, String server) {
		return getPermissions(group, server, false);
	}

	public List<String> getPermissions(String group, final String paramServer, final boolean global) {
		group = group.toLowerCase();
		final List<String> permissions = new ArrayList<>();
		executeQuery(SELECT_PERMISSIONS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(2);
					if (server.equalsIgnoreCase(paramServer) || (server.equalsIgnoreCase("global") && global)) {
						permissions.add(resultSet.getString(4));
					}
				}
			}
		}, new ColumnVarChar("group", 50, group));
		debug("Fetched permissions for `" + group + "` on server `" + (global ? "GLOBAL|" : "") + paramServer
				+ "`. List: " + permissions);
		return permissions;
	}

	public void addPermission(String group, String permission) {
		group = group.toLowerCase();
		permission = permission.toLowerCase();
		executeUpdate(INSERT_PERMISSION, new ColumnVarChar("server", 50, server), new ColumnVarChar("group", 50, group),
				new ColumnVarChar("permission", 100, permission));
		debug("Added permission `" + permission + "` from `" + group + "`");
	}

	public void removePermission(String group, String permission) {
		group = group.toLowerCase();
		permission = permission.toLowerCase();
		executeUpdate(DELETE_PERMISSION, new ColumnVarChar("server", 50, server), new ColumnVarChar("group", 50, group),
				new ColumnVarChar("permission", 100, permission));
		debug("Removed permission `" + permission + "` from `" + group + "`");
	}

	// XXX Groups

	public String groupExist(String name) {
		HashMap<String, List<PermissionGroup>> map = getGlobalGroups();
		for (String server : map.keySet()) {
			for (PermissionGroup group : map.get(server)) {
				if (group.getName().equalsIgnoreCase(name)) {
					return server;
				}
			}
		}
		return null;
	}

	public PermissionGroup getGroup(String name) {
		HashMap<String, List<PermissionGroup>> map = getGlobalGroups();
		for (String server : map.keySet()) {
			for (PermissionGroup group : map.get(server)) {
				if (group.getName().equalsIgnoreCase(name)) {
					return group;
				}
			}
		}
		return null;
	}

	public HashMap<String, List<PermissionGroup>> getGlobalGroups() {
		final HashMap<String, List<PermissionGroup>> groups = new HashMap<>();
		executeQuery(SELECT_GROUPS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(2);
					if (!groups.containsKey(server))
						groups.put(server, new ArrayList<PermissionGroup>());
					PermissionGroup group = new PermissionGroup(resultSet.getString(3));
					group.setPrefix(resultSet.getString(4));
					group.setSuffix(resultSet.getString(5));
					if (resultSet.getBoolean(6)) {
						group.setDefault();
					}
					groups.get(server).add(group);
				}
			}
		});
		debug("Groups fetched. Map: " + groups);
		return groups;
	}

	public List<PermissionGroup> getGroups() {
		final List<PermissionGroup> groups = new ArrayList<>();
		executeQuery(SELECT_GROUPS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					String server = resultSet.getString(2);
					if (server.equalsIgnoreCase(GroupDatabase.this.server) || server.equalsIgnoreCase("global")) {
						PermissionGroup group = new PermissionGroup(resultSet.getString(3));
						group.setPrefix(resultSet.getString(4));
						group.setSuffix(resultSet.getString(5));
						if (resultSet.getBoolean(6)) {
							group.setDefault();
						}
						groups.add(group);
					}
				}
			}
		});
		debug("Fetched groups: " + groups);
		return groups;
	}

	public void createGroup(String group) {
		createGroup(group, null, null, false);
	}

	public void createGroup(String group, String prefix, String suffix, boolean def) {
		group = group.toLowerCase();
		executeUpdate(CREATE_GROUP, new ColumnVarChar("server", 50, (def ? "global" : server)),
				new ColumnVarChar("group", 50, group), new ColumnVarChar("prefix", 100, prefix),
				new ColumnVarChar("suffix", 100, suffix), new ColumnBoolean("default", def));
		debug("Created group `" + group + "` attributes Prefix=" + prefix + ",Suffix=" + suffix + ",Default=" + def);
	}

	public void deleteGroup(String group) {
		group = group.toLowerCase();
		executeUpdate(DELETE_GROUP, new ColumnVarChar("group", 50, group), new ColumnVarChar("server", 50, server));
		debug("Deleted group `" + group + "`");
	}

	public void changeServer(String group, String server) {
		group = group.toLowerCase();
		executeUpdate(UPDATE_SERVER, new ColumnVarChar("server", server.length(), server),
				new ColumnVarChar("group", 50, group));
		debug("Updated `" + group + "`'s loading server to `" + server + "`");
	}

	public void updatePrefix(String group, String prefix) {
		group = group.toLowerCase();
		executeUpdate(UPDATE_PREFIX, new ColumnVarChar("prefix", 100, prefix), new ColumnVarChar("group", 50, group));
		debug("Updated `" + group + "`'s prefix to `" + prefix + "`");
	}

	public void updateSuffix(String group, String suffix) {
		group = group.toLowerCase();
		executeUpdate(UPDATE_SUFFIX, new ColumnVarChar("suffix", 100, suffix), new ColumnVarChar("server", 50, server),
				new ColumnVarChar("group", 50, group));
		debug("Updated `" + group + "`'s suffix to `" + suffix + "`");
	}

	// XXX Initialization

	@Override
	protected void initialize() {
		executeUpdate(CREATE_GROUPS_TABLE);
		executeUpdate(CREATE_PERMISSIONS_TABLE);
		executeUpdate(CREATE_INHERITANCE_TABLE);
	}

	@Override
	protected void update() {
	}
}