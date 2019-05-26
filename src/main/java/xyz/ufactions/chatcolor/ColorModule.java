package xyz.ufactions.chatcolor;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.Module;
import xyz.ufactions.chatcolor.command.ColorCommand;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ColorModule extends Module {
    
    private HashMap<Player, ChatColor> colors = new HashMap<>();

    public ColorModule(JavaPlugin plugin) {
        super("ChatColor", plugin);
    }

    @Override
    public void addCommands() {
        addCommand(new ColorCommand(this));
    }

    public void setColor(Player player, ChatColor color) {
        colors.put(player, color);
    }

    public ChatColor getColor(Player player) {
        if(!colors.containsKey(player)) colors.put(player, ChatColor.WHITE);
        return colors.get(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        colors.put(player, fetch(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        File file = new File(getPlugin().getDataFolder() + "/color/config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(Exception e1) {
                e1.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> temp = config.getStringList("colors");
        Player player = e.getPlayer();
        temp.add(encode(player, colors.remove(player)));
        config.set("colors", temp);
        try {
            config.save(file);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage(getColor(e.getPlayer()) + e.getMessage());
    }

    private String encode(Player player, ChatColor color) {
        return player.getUniqueId() + ":" + color.name();
    }

    private ChatColor fetch(Player player) {
        File file = new File(getPlugin().getDataFolder() + "/color/config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(Exception e1) {
                e1.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> temp = config.getStringList("colors");
        for(String string : temp) {
            String[] array = string.split(":");
            if(array[0].equals(player.getUniqueId().toString())) {
                return ChatColor.valueOf(array[1]);
            }
        }
        return ChatColor.WHITE;
    }
}
