package xyz.ufactions.transporter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.*;
import xyz.ufactions.redis.connect.RedisTransferManager;
import xyz.ufactions.selections.SelectionManager;
import xyz.ufactions.selections.data.Selection;
import xyz.ufactions.transporter.commands.HubCommand;
import xyz.ufactions.transporter.commands.PortalCommand;
import xyz.ufactions.transporter.ui.ServerUI;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TransporterModule extends Module {

    private SelectionManager selectionManager;

    private Map<Selection, String> portals = new HashMap<>();

    private Map<Player, Long> transferring = new HashMap<>();


    public TransporterModule(JavaPlugin plugin, SelectionManager selectionManager) {
        super("Transporter", selectionManager.getPlugin());

        this.selectionManager = selectionManager;

        File file = new File(getDataFolder(), "data.yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String index : config.getConfigurationSection("portals").getKeys(false)) {
                String path = "portals." + index;
                portals.put(Selection.deserialize(config.getString(path + ".selection")), config.getString(path + ".destination"));
            }
        }

        RedisTransferManager.initialize(plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            giveTeleporter(player);
        }
    }

    @Override
    public void addCommands() {
        addCommand(new PortalCommand(this));
        addCommand(new HubCommand(this));
    }

    public void transfer(String player, String destination) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            if (transferring.containsKey(p)) {
                UtilPlayer.message(p, F.error(getName(), "You are already transferring!"));
                return;
            }
            UtilPlayer.message(p, F.main(getName(), "Transferring you to " + F.elem(destination) + "..."));
            transferring.put(p, System.currentTimeMillis());
        }
        RedisTransferManager.getInstance().transfer(player, destination);
    }

    public void setDestination(Selection selection, String destination) {
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int index = config.getInt("index");
        config.set("portals." + index + ".selection", selection.serialize());
        config.set("portals." + index + ".destination", destination);
        config.set("index", ++index);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        portals.put(selection, destination);
    }

    @EventHandler
    public void transferUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        for (Player player : transferring.keySet()) {
            if (!player.isOnline() || !player.isValid()) {
                transferring.remove(player);
                continue;
            }
            if (UtilTime.elapsed(transferring.get(player), 5000)) {
                transferring.remove(player);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        giveTeleporter(e.getPlayer());
    }

    @EventHandler
    public void onPortalEnter(PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            Block block = e.getTo().getBlock();
            if (block.getType() == Material.WATER || block.getRelative(BlockFace.UP).getType() == Material.WATER ||
                    block.getType() == Material.STATIONARY_WATER || block.getRelative(BlockFace.UP).getType() == Material.STATIONARY_WATER) {
                Player player = e.getPlayer();
                if (!isTransferring(player)) {
                    for (Selection selection : portals.keySet()) {
                        if (selection.isInside(block.getLocation())) {
                            transfer(player.getName(), portals.get(selection));
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onServerCompassClick(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            ItemStack item = e.getItem();
            if (item.getType() == Material.COMPASS) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equals(C.cAqua + C.Bold + "Server Compass")) {
                        if (UtilEvent.isAction(e, UtilEvent.ActionType.R) || UtilEvent.isAction(e, UtilEvent.ActionType.L)) {
                            new ServerUI(this).openInventory(e.getPlayer());
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public void giveTeleporter(Player player) {
        if (Bukkit.getServerName().equalsIgnoreCase("hub")) {
            player.getInventory().setItem(4, new ItemBuilder(Material.COMPASS).name(C.cAqua
                    + C.Bold + "Server Compass").glow(true).build());
        }
    }

    public boolean isTransferring(Player player) {
        return transferring.containsKey(player);
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}