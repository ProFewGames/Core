package xyz.ufactions.permissions.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.libs.UtilTime;
import xyz.ufactions.permissions.PermissionsModule;
import xyz.ufactions.permissions.data.PermissionGroup;
import xyz.ufactions.permissions.data.PermissionUser;
import xyz.ufactions.permissions.database.GroupDatabase;
import xyz.ufactions.permissions.database.PermissionDatabase;
import xyz.ufactions.permissions.events.PlayerGroupUpdateEvent;
import xyz.ufactions.permissions.events.PlayerPermissionsLoginEvent;

public class PermissionManager {

	private final PermissionsModule plugin;
	private final PermissionDatabase repository;
	private final GroupDatabase groupRepository;

	private PermissionGroup FALLBACK_GROUP;

	private HashMap<UUID, PermissionUser> users = new HashMap<>();
	private HashMap<UUID, PermissionAttachment> data = new HashMap<>();
	private List<PermissionGroup> groups = new ArrayList<>();
	private HashSet<UUID> loggingIn = new HashSet<>();
	private boolean debug = false;

	private HashSet<UUID> lockLogin = new HashSet<>();
	private boolean loadLock = false;
	private long lockTime = 0L;

	private boolean shutdownLock = false;

	public void tick() {
		if (shutdownLock)
			return;
		if (loadLock) {
			if (UtilTime.elapsed(lockTime, 15000)) {
				System.err.println("Login Error " + UtilTime.MakeStr(System.currentTimeMillis() - lockTime, 4));
				for (Player player : UtilServer.getPlayers()) {
					if (player != null) {
						player.kickPlayer(C.cRed + "*login error*");
					}
				}
				shutdownLock = true;
				plugin.runSyncLater(new Runnable() {

					@Override
					public void run() {
						Bukkit.shutdown();
					}
				}, 10L);
			}
		} else if (!lockLogin.isEmpty()) {
			debug("Login Lock Disabled");
			Iterator<UUID> iterator = lockLogin.iterator();
			while (iterator.hasNext()) {
				login(iterator.next());
			}
			lockLogin.clear();
		}
	}

	public PermissionManager(PermissionsModule plugin, String server_name) {
		this.plugin = plugin;
		this.repository = new PermissionDatabase(plugin.getPlugin(), server_name);
		this.groupRepository = new GroupDatabase(plugin.getPlugin(), server_name);

		checkForFile();

		FALLBACK_GROUP = new PermissionGroup("FALLBACK", "&4&lFALLBACK", "", new ArrayList<String>(), new ArrayList<String>());

		debug = new File("perm_debug.dat").exists();
	}

	public void login(final UUID uuid) {
		if (loadLock) {
			if (UtilTime.elapsed(lockTime, 10000)) {
				System.err.println("System spending too much time in lock.");
			}
			if (!lockLogin.contains(uuid))
				lockLogin.add(uuid);
			return;
		}
		if (loggingIn.contains(uuid)) {
			return;
		}
		if (users.containsKey(uuid)) {
			users.remove(uuid);
		}
		loggingIn.add(uuid);
		final PermissionUser user = new PermissionUser();
		users.put(uuid, user);
		debug(uuid + " logging in...");

		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				try {
					PermissionUser user = users.get(uuid);
					user.setPermissions(repository.getPermissions(uuid));
					List<String> playerGroups = repository.getGroups(uuid);
					if (playerGroups.isEmpty()) {
						debug(uuid + " has no group saved, defaulting...");
						PermissionGroup defaultGroup = getDefGroup();
						user.addGroup(defaultGroup);
					} else {
						debug("Saving repository groups internally...");
						for (String playerGroup : playerGroups) {
							for (PermissionGroup group : groups) {
								if (group.getName().equalsIgnoreCase(playerGroup)) {
									user.addGroup(group);
								}
							}
						}
					}
				} catch (Exception e) {
					File a = new File("errors/");
					if (!a.exists())
						a.mkdirs();
					final int id;
					File file = new File(a, "errorId.yml");
					if (!file.exists()) {
						id = 0;
						try {
							file.createNewFile();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else {
						FileConfiguration config = YamlConfiguration.loadConfiguration(file);
						id = config.getInt("Index");
						config.set("Index", Integer.valueOf(id + 1));
						try {
							config.save(file);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					plugin.runSyncLater(new Runnable() {

						@Override
						public void run() {
							final Player player = Bukkit.getPlayer(uuid);
							if (player != null) {
								plugin.runSync(new Runnable() {

									@Override
									public void run() {
										player.kickPlayer(C.cRed + "Login Error. Error ID " + id);
									}
								});
								System.out.println(player.getName()
										+ " has been kicked because there was a login error. Error ID " + id);
							}
						}
					}, 10L);
					File error = new File(a, "Error_" + id + ".dat");
					if (!error.exists()) {
						try {
							error.createNewFile();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					try {
						BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(error));
						for (StackTraceElement element : e.getStackTrace()) {
							bufferedWriter.write(element.toString() + "\n");
						}
						bufferedWriter.close();
					} catch (Exception ex) {
						System.out.println(" ");
						System.out.println("Writing error:");
						System.out.println(" ");
						ex.printStackTrace();
						System.out.println(" ");
						System.out.println("Login Error:");
						System.out.println(" ");
						e.printStackTrace();
					}
				}
				plugin.getScheduler().runTask(plugin.getPlugin(), new Runnable() {

					@Override
					public void run() {
						loggingIn.remove(uuid);
						debug(uuid + " Logged in!");
						Bukkit.getPluginManager().callEvent(new PlayerGroupUpdateEvent(uuid, null, PlayerGroupUpdateEvent.GroupUpdateType.ADD));
						Bukkit.getServer().getPluginManager().callEvent(new PlayerPermissionsLoginEvent(uuid, user));
					}
				});
			}
		});
	}

	public PermissionDatabase getRepository() {
		return repository;
	}

	public GroupDatabase getGroupRepository() {
		return groupRepository;
	}

	public void savePermissions(final Player player) {
		final PermissionUser user = users.get(player.getUniqueId());
		plugin.runAsync(new Runnable() {

			public void run() {
				repository.saveUser(player, user);
			}
		});
	}

	public PermissionUser get(Player player) {
		if (!users.containsKey(player.getUniqueId())) {
			login(player.getUniqueId());
		}
		return users.get(player.getUniqueId());
	}

	public List<String> getPermissions(UUID uuid) {
		return repository.getPermissions(uuid);
	}

	public PermissionAttachment getAttachment(Player player) {
		UUID uuid = player.getUniqueId();
		if (!data.containsKey(uuid)) {
			data.put(uuid, player.addAttachment(plugin.getPlugin()));
		}
		return data.get(uuid);
	}

	public void unload(Player player) {
		if (data.containsKey(player.getUniqueId())) {
			data.get(player.getUniqueId()).remove();
		}
		data.remove(player.getUniqueId());
		users.remove(player.getUniqueId());
	}

	public void refresh(Player player) {
		if (data.containsKey(player.getUniqueId())) {
			data.get(player.getUniqueId()).remove();
		}
		data.remove(player.getUniqueId());
		login(player.getUniqueId());
	}

	public void inject(Player player) {
		PermissionUser user = get(player);
		for (PermissionGroup group : user.getGroups()) {
			injectGroup(player, group);
		}
		for (String permission : user.getPermissions()) {
			inject(player, permission);
		}
	}

	private void injectGroup(Player player, PermissionGroup group) {
		for (String childName : group.getInheritance()) {
			PermissionGroup child = getGroup(childName);
			injectGroup(player, child);
		}
		for (String permission : group.getPermissions()) {
			inject(player, permission);
		}
	}

	public void inject(Player player, String perm) {
		if (perm.equals("*")) {
			injectAll(player);
			return;
		}
		boolean value = perm.startsWith("-") ? false : true;
		if (!value) {
			perm = perm.replace("-", "");
		}
		getAttachment(player).setPermission(perm, value);
		debug((value ? "Injected " : "Removed ") + "permission \"" + perm + "\"" + (value ? " to " : " from ") + "user "
				+ player.getName());
	}

	private void injectAll(final Player player) {
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				debug("Injecting " + player.getName() + " with all permissions");
				for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
					String perm = permission.getName();
					boolean value = perm.startsWith("-") ? false : true;
					if (!value) {
						perm = perm.replace("-", "");
					}
					getAttachment(player).setPermission(perm, value);
				}
				debug(player.getName() + " injected with all permissions");
			}
		});
	}

	public List<String> getGroups(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			List<String> a = new ArrayList<>();
			for (PermissionGroup b : get(player).getGroups()) {
				a.add(b.getName());
			}
			return a;
		}
		return repository.getGroups(uuid);
	}

	public void addPermission(UUID uuid, String perm) {
		repository.addPermission(uuid, perm);
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			inject(player, perm);
		}
	}

	public void removePermission(UUID uuid, String perm) {
		repository.deletePermission(uuid, perm);
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			refresh(player);
		}
	}

	private void debug(String string) {
		if (debug)
			System.out.println("[PermissionManager] " + string);
	}

	private void checkForFile() {
		File file = new File(plugin.getPlugin().getDataFolder(), "groups.yml");
		if (file.exists()) {
			debug("groups.yml file found! Moving file over to the mysql database.");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			if (config.isConfigurationSection("groups")) {
				debug("Iterating through groups...");
				String ports = "";
				for (String name : config.getConfigurationSection("groups").getKeys(false)) {
					debug("Porting " + name + "...");

					String path = "groups." + name;
					groupRepository.createGroup(name, config.getString(path + ".prefix"),
							config.getString(path + ".suffix"), config.getBoolean(path + ".default"));

					debug("Porting permissions for " + name
							+ ", this might take a while depending on how many permissions are on this group.");
					for (String permission : config.getStringList(path + ".permissions")) {
						groupRepository.addPermission(name, permission);
					}

					debug("Porting inheritance of " + name);
					for (String child : config.getStringList(path + ".inheritance")) {
						groupRepository.addChild(name, child);
					}

					ports += name + " ";
					debug(name + " ported to MySQL successfully.");
				}
				debug("Successfully ported " + ports + " to MySQL");
				file.renameTo(new File(plugin.getPlugin().getDataFolder(), file.getName() + ".tmp"));
				debug("groups.yml has been renamed to prevent dupping in the system, you may access it later if you wish.");
			}
		}
	}

	public void loadGroups() {
		debug("Locking the login quota");
		loadLock = true;
		lockTime = System.currentTimeMillis();
		groups.clear();
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				try {
					groups.addAll(groupRepository.loadGroups());
				} catch (Exception e) {
					System.out.println("There was an error loading groups");
					System.out.println("Stacktrace:");
					e.printStackTrace();
					System.out.println("Routing to default group.");
					PermissionGroup group = new PermissionGroup("default");
					group.setDefault();
					groups.add(group);
				}
				loadLock = false;
				lockTime = 0L;
				debug("Login Lock Disabled (Loaded Groups)");
			}
		});
	}

	public void createGroup(final String name) {
		if (getGroup(name) != null)
			return;
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.createGroup(name);
			}
		});
		groups.add(new PermissionGroup(name));
	}

	public void setPrefix(final PermissionGroup group, final String prefix) {
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.updatePrefix(group.getName(), prefix);
			}
		});
		group.setPrefix(prefix);
	}

	public void setSuffix(final PermissionGroup group, final String suffix) {
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.updateSuffix(group.getName(), suffix);
			}
		});
		group.setSuffix(suffix);
	}

	public List<String> getInheritance(String groupName) {
		PermissionGroup group = getGroup(groupName);
		if (group == null)
			return new ArrayList<>();
		return group.getInheritance();
	}

	public PermissionGroup getGroup(String group) {
		for (PermissionGroup g : groups) {
			if (g.getName().equalsIgnoreCase(group)) {
				return g;
			}
		}
		return null;
	}

	public boolean groupExists(String groupName) {
		for (PermissionGroup group : groups) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				return true;
			}
		}
		return false;
	}

	public String findGroup(String startWith) {
		List<String> groups = new ArrayList<>();
		for (String group : getGroups()) {
			if (group.equalsIgnoreCase(startWith))
				return group;
			if (group.toUpperCase().startsWith(startWith.toUpperCase())) {
				groups.add(group);
			}
		}
		if (groups.size() == 1) {
			return groups.get(0);
		}
		return getDefaultGroup();
	}

	public void addGroup(final UUID uuid, final String group) {
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				repository.addGroup(uuid, findGroup(group), true);
			}
		});
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			refresh(player);
		}
		// Calling event to update certain objects in the code from other
		// modules.
		Bukkit.getPluginManager().callEvent(new PlayerGroupUpdateEvent(uuid, group, PlayerGroupUpdateEvent.GroupUpdateType.ADD));
	}

	public void setGroup(final UUID uuid, final String group) {
		// Running everything async so it doesn't lag the server
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				// Removing previous groups from the repository
				for (String group : repository.getGroups(uuid)) {
					repository.removeGroup(uuid, group);
				}

				// Adding the new group to the repository
				repository.addGroup(uuid, findGroup(group), true);

				// Refreshing the player if he/she is online so their
				// permissions can stay up-to-date
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					refresh(player);
				}

				// Running the event sync'd
				Bukkit.getScheduler().runTask(plugin.getPlugin(), new Runnable() {

					@Override
					public void run() {
						Bukkit.getPluginManager()
								.callEvent(new PlayerGroupUpdateEvent(uuid, group, PlayerGroupUpdateEvent.GroupUpdateType.ADD));

					}
				});
			}
		});
	}

	public void removeGroup(final UUID uuid, final String group) {
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				repository.removeGroup(uuid, group);
			}
		});
		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			refresh(player);
		}
		// Calling event to update certain objects in the code from other
		// modules.
		Bukkit.getPluginManager().callEvent(new PlayerGroupUpdateEvent(uuid, group, PlayerGroupUpdateEvent.GroupUpdateType.REMOVE));
	}

	private boolean b(List<String> b, String c) {
		for (String d : b) {
			if (d.equalsIgnoreCase(c)) {
				return true;
			}
		}
		return false;
	}

	private String a(List<String> b, String c) {
		for (String d : b) {
			if (d.equalsIgnoreCase(c)) {
				return d;
			}
		}
		return c;
	}

	public boolean inGroupWithChild(Player player, String groupName) {
		if (get(player).getGroups().isEmpty()) {
			if (getGroup(groupName).isDefault()) {
				return true;
			}
		}
		for (PermissionGroup group : get(player).getGroups()) {
			if (group.getName().equalsIgnoreCase(groupName)) {
				return true;
			} else {
				for (String childName : group.getInheritance()) {
					PermissionGroup child = getGroup(childName);
					if (child != null) {
						if (child.getName().equalsIgnoreCase(groupName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void removePermission(final String group, final String permission) {
		PermissionGroup g = getGroup(group);
		if (g != null) {
			g.removePermission(a(g.getPermissions(), permission));
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (inGroupWithChild(player, group)) {
					refresh(player);
				}
			}
		}
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.removePermission(group, a(groupRepository.getPermissions(group), permission)); // XXX
																												// Potentially
																												// unsafe?
			}
		});
	}

	public void addPermission(final String group, final String permission) {
		PermissionGroup g = getGroup(group);
		if (g != null) {
			if (!b(g.getPermissions(), permission)) {
				g.addPermission(permission);
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (inGroupWithChild(player, group)) {
						refresh(player);
					}
				}
			}
		}
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				if (!b(groupRepository.getPermissions(group), permission)) { // XXX
																				// Potentially
																				// unsafe?
					groupRepository.addPermission(group, permission);
				}
			}
		});
	}

	public String buildPrefix(OfflinePlayer player) {
		if (player.isOnline()) {
			String prefix = get(player.getPlayer()).buildPrefix();
			if (prefix.equals("")) {
				prefix = getDefGroup().getPrefix() + " ";
			}
			return plugin.getTitleModule().getTagManager().getTagFormatted(player.getPlayer()) + prefix;
		} else {
			return "";
		}
	}

	public String buildSuffix(OfflinePlayer player) {
		if (player.isOnline()) {
			return get(player.getPlayer()).buildSuffix();
		} else {
			return "";
		}
	}

	public String getPrefix(String group) {
		for (PermissionGroup g : groups) {
			if (g.getName().equalsIgnoreCase(group)) {
				return g.getPrefix();
			}
		}
		return "";
	}

	public String getSuffix(String group) {
		for (PermissionGroup g : groups) {
			if (g.getName().equalsIgnoreCase(group)) {
				return g.getSuffix();
			}
		}
		return "";
	}

	public boolean inGroup(OfflinePlayer player, String group) {
		if (player.isOnline()) {
			return get(player.getPlayer()).isInGroup(group);
		}
		return false;
	}

	public boolean hasPermission(OfflinePlayer player, String permission) {
		if (player.isOnline()) {
			return get(player.getPlayer()).getPermissions()
					.contains(a(get(player.getPlayer()).getPermissions(), permission));
		}
		List<String> perms = repository.getPermissions(player.getUniqueId());
		return perms.contains(a(perms, permission));
	}

	public List<String> getGroups() {
		List<String> gps = new ArrayList<>();
		for (PermissionGroup g : groups) {
			gps.add(g.getName());
		}
		return gps;
	}

	public boolean hasPermission(String group, String permission) {
		PermissionGroup g = getGroup(group);
		if (g != null) {
			return g.getPermissions().contains(a(g.getPermissions(), permission));
		}
		return false;
	}

	public String getDefaultGroup() {
		for (PermissionGroup group : groups) {
			if (group.isDefault()) {
				return group.getName();
			}
		}
		return "";
	}

	public PermissionGroup getDefGroup() {
		for (PermissionGroup group : groups) {
			if (group.isDefault()) {
				return group;
			}
		}
		return FALLBACK_GROUP;
	}

	public void deleteGroup(String name) {
		final PermissionGroup group = getGroup(name);
		if (group == null)
			return;
		for (Player player : Bukkit.getOnlinePlayers()) {
			removeGroup(player.getUniqueId(), name);
		}
		groups.remove(group);
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.deleteGroup(group.getName());
			}
		});
	}

	public void addChild(String parentName, final String childName) {
		final PermissionGroup parent = getGroup(parentName);
		if (parent == null)
			return;
		parent.addInheritance(childName);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (inGroupWithChild(player, parentName)) {
				refresh(player);
			}
		}
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				if (!b(groupRepository.getInheritance(parent.getName()), childName)) {
					groupRepository.addChild(parent.getName(), childName);
				}
			}
		});
	}

	public void removeChild(String parentName, final String childName) {
		final PermissionGroup parent = getGroup(parentName);
		if (parent == null)
			return;
		parent.removeInheritance(a(parent.getInheritance(), childName));
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (inGroupWithChild(player, parentName)) {
				refresh(player);
			}
		}
		plugin.runAsync(new Runnable() {

			@Override
			public void run() {
				groupRepository.removeChild(parent.getName(),
						a(groupRepository.getInheritance(parent.getName()), childName));
			}
		});
	}

	public String getPrimaryGroup(Player player) {
		if (getGroups(player.getUniqueId()).isEmpty()) {
			return getDefaultGroup();
		}
		return getGroups(player.getUniqueId()).get(0);
	}
}