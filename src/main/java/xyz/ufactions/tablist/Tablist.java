package xyz.ufactions.tablist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import xyz.ufactions.api.Module;
import xyz.ufactions.core.ModuleManager;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.TitleAPI;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.tablist.commands.TablistCommand;
import xyz.ufactions.tablist.repository.TablistRepository;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class Tablist extends Module {

	private List<Player> processing = new ArrayList<>();

	private TablistRepository repository;

	private String serverName = "A Minecraft Server";

	public Tablist(JavaPlugin plugin, String serverName) {
		super("Tablist", plugin);

		this.serverName = serverName;

		this.repository = new TablistRepository(plugin);

		reload();
	}

	public void reload() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			overHead(player);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		reload();
	}

	@Override
	public void addCommands() {
		addCommand(new TablistCommand(this));
	}

	public TablistRepository getRepository() {
		return repository;
	}

	private void headerFooter() {
		for (Player pls : UtilServer.getPlayers()) {
			String header = C.mHead + C.Bold + serverName;
			String footer = C.cGray + "(" + Bukkit.getOnlinePlayers().size() + " players)";
			TitleAPI.sendTabHF(pls, header, footer);
		}
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getType() != UpdateType.SLOWER)
			return;

		headerFooter();
	}

	private void overHead(Player player) {
		runAsync(new Runnable() {

			@Override
			public void run() {
				if (processing.contains(player))
					return;
				processing.add(player);

				headerFooter();

				Scoreboard scoreboard = player.getScoreboard();

				HashMap<String, String> tablist = repository.getTablist();
				for(PermissionGroup group : PermissionsEx.getPermissionManager().getGroups()) {
					String groupName = group.getName();
					if (scoreboard.getTeam(groupName) != null) {
						scoreboard.getTeam(groupName).unregister();
					}
					String prefix = tablist.get(groupName);
					if (prefix == null)
						prefix = "&4&lN/A&r";
					prefix = ChatColor.translateAlternateColorCodes('&', prefix) + " ";
					scoreboard.registerNewTeam(groupName).setPrefix(prefix);
				}

				String group = PermissionsEx.getUser(player).getGroups()[0].getName();
				validate(player, group, scoreboard);

				for (Player otherPlayer : UtilServer.getPlayers()) {
					String otherGroup = PermissionsEx.getUser(otherPlayer).getGroups()[0].getName();
					validate(otherPlayer, otherGroup, scoreboard);
					if (otherPlayer.getScoreboard() == null || otherPlayer.getScoreboard().getTeam(group) == null) {
						overHead(otherPlayer);
					}
					validate(player, group, otherPlayer.getScoreboard());
				}
				processing.remove(player);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void validate(Player player, String group, Scoreboard scoreboard) {
		// FIXME Default group not setting
		try {
			if (scoreboard.getTeam(group) == null) {
				scoreboard.getTeam(PermissionsEx.getPermissionManager().getGroups()[0].getName()).addPlayer(player);
			} else {
				scoreboard.getTeam(group).addPlayer(player);
			}
		}
		catch(Exception e) {}
	}
}