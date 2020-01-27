package xyz.ufactions.permissions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.permissions.command.GlobalPermissionCommand;
import xyz.ufactions.permissions.command.PermissionCommand;
import xyz.ufactions.permissions.command.RankCommand;
import xyz.ufactions.permissions.data.PermissionChat;
import xyz.ufactions.permissions.data.PermissionVault;
import xyz.ufactions.permissions.events.PlayerPermissionsLoginEvent;
import xyz.ufactions.permissions.manager.PermissionManager;
import xyz.ufactions.tags.TitleModule;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class PermissionsModule extends Module {

	private PermissionManager manager;
	private TitleModule titleModule;

	public PermissionsModule(JavaPlugin plugin) {
		super("Permissions", plugin);
		log("=[ Arkham Permissions Loading... ]=");
		new MPC(this);
		manager = new PermissionManager(this, "MegaqCore"); // TODO Add jedis support to MegaBukkit and revert changes to network support
		manager.loadGroups();
		PermissionVault vault = new PermissionVault(plugin, this);
		getPlugin().getServer().getServicesManager().register(net.milkbowl.vault.permission.Permission.class, vault,
				plugin, ServicePriority.Normal);
		getPlugin().getServer().getServicesManager().register(net.milkbowl.vault.chat.Chat.class,
				new PermissionChat(vault, this), plugin, ServicePriority.Normal);
		log("=[ Arkham Permissions Loaded! ]=");
		for (Player player : Bukkit.getOnlinePlayers()) {
			manager.login(player.getUniqueId());
		}
	}

	public void setTitleModule(TitleModule titleModule) {
		this.titleModule = titleModule;
	}

	public TitleModule getTitleModule() {
		return titleModule;
	}

	public PermissionManager getPermissionsManager() {
		return manager;
	}

	@Override
	public void addCommands() {
		addCommand(new PermissionCommand(this));
		addCommand(new RankCommand(this));
		addCommand(new GlobalPermissionCommand(this));
	}

	// XXX Bring back potentially if this causes problems with players
	// @Override
	// public void disable() {
	// for (Player player : Bukkit.getOnlinePlayers()) {
	// manager.savePermissions(player);
	// }
	// }

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		manager.login(e.getUniqueId());
	}

	@EventHandler
	public void onPermissionsLogin(PlayerPermissionsLoginEvent e) {
		Player player = Bukkit.getPlayer(e.getUniqueId());
		if (player != null) {
			manager.inject(player);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		manager.savePermissions(e.getPlayer());
		manager.unload(e.getPlayer());
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.TICK)
			return;
		manager.tick();
	}

	public String getPrefix(String group) {
		return manager.getPrefix(group).replace(">>", "");
	}
}