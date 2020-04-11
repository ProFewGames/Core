package xyz.ufactions.tablist;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.TitleAPI;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.tablist.commands.TablistCommand;
import xyz.ufactions.tablist.repository.TablistRepository;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tablist extends Module {

    private List<Player> processing = new ArrayList<>();

    private TablistRepository repository;

    private String serverName = "A Minecraft Server";

    private Permission permission;

    public Tablist(JavaPlugin plugin, String serverName, Permission permission) {
        super("Tablist", plugin);

        this.serverName = serverName;

        this.permission = permission;

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
                for (String group : permission.getGroups()) {
                    if (scoreboard.getTeam(group) != null) {
                        scoreboard.getTeam(group).unregister();
                    }
                    String prefix = tablist.get(group);
                    if (prefix == null) prefix = "&4&lN/A";
                    prefix = ChatColor.translateAlternateColorCodes('&', prefix);
                    scoreboard.registerNewTeam(group).setPrefix(prefix);
                }
                String group = permission.getPrimaryGroup(player);
                validate(player, group, scoreboard);

                for (Player otherPlayer : UtilServer.getPlayers()) {
                    String otherGroup = permission.getPrimaryGroup(otherPlayer);
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
                scoreboard.getTeam(permission.getGroups()[0]).addPlayer(player);
            } else {
                scoreboard.getTeam(group).addPlayer(player);
            }
        } catch (Exception e) {
        }
    }
}