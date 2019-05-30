package xyz.ufactions.chatcolor;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.chatcolor.command.ColorCommand;
import xyz.ufactions.chatcolor.repository.ColorRepository;
import xyz.ufactions.libs.UtilServer;

import java.util.HashMap;

public class ColorModule extends Module {

    private ColorRepository repository;
    private HashMap<Player, ChatColor> colors = new HashMap<>();

    public ColorModule(JavaPlugin plugin) {
        super("ChatColor", plugin);

        repository = new ColorRepository(plugin);

        for(Player player : UtilServer.getPlayers()) {
            login(player);
        }
    }

    @Override
    public void addCommands() {
        addCommand(new ColorCommand(this));
    }

    public void setColor(final Player player, final ChatColor color) {
        colors.put(player, color);
        runAsync(new Runnable() {
            @Override
            public void run() {
                repository.setColor(player.getUniqueId(), color);
            }
        });
    }

    public ChatColor getColor(Player player) {
        if(!colors.containsKey(player)) colors.put(player, ChatColor.WHITE);
        return colors.get(player);
    }

    private void login(final Player player) {
        runAsync(new Runnable() {
            @Override
            public void run() {
                colors.put(player, repository.getColor(player.getUniqueId()));
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        login(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        colors.remove(e.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage(getColor(e.getPlayer()) + e.getMessage());
    }
}
