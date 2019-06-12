package xyz.ufactions.builder;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.builder.command.BuilderCommand;
import xyz.ufactions.libs.*;
import xyz.ufactions.libs.UtilEvent.ActionType;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class BuilderModule extends Module {

    private HashMap<String, BuilderPlayer> builders = new HashMap<>();

    public BuilderModule(JavaPlugin plugin) {
        super("Builder", plugin);
    }

    @Override
    public void disable() {
        for (BuilderPlayer player : builders.values()) {
            player.reset();
        }
    }

    @Override
    public void addCommands() {
        addCommand(new BuilderCommand(this));
    }

    public boolean toggleBuilderMode(Player player) {
        if (builders.containsKey(player.getName())) {
            BuilderPlayer builderPlayer = builders.remove(player.getName());
            builderPlayer.reset();
            return false;
        } else {
            builders.put(player.getName(), new BuilderPlayer(player));
            return true;
        }
    }

    public boolean isBuilderMode(Player player) {
        return builders.containsKey(player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (isBuilderMode(player)) {
            toggleBuilderMode(player);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SLOWER) return;

        for (String name : new HashMap<>(builders).keySet()) {
            Player player = Bukkit.getPlayer(name);
            BuilderPlayer bp = builders.get(name);
            if (!UtilInv.contains(player, C.cAqua + C.Bold + "WorldEdit Wand", Material.WOOD_AXE, (byte) 0, 1)
                    || !UtilInv.contains(player, C.cAqua + C.Bold + "Next Page", Material.PAPER, (byte) 0, 1)) {
                bp.reEquipt();
                UtilPlayer.message(player, F.main(Plugin.getName(), "Your inventory has been reequipped with proper utensils!"));
            } else if (bp.getCurrentPage() > 0 && (!UtilInv.contains(player, C.cAqua + C.Bold + "Previous Page", Material.PAPER, (byte) 0, 1))) {
                bp.reEquipt();
                UtilPlayer.message(player, F.main(Plugin.getName(), "Your inventory has been reequipped with proper utensils!"));
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (isBuilderMode(e.getPlayer())) {
            if (UtilInv.IsItem(e.getItemDrop().getItemStack(), C.cAqua + C.Bold + "WorldEdit Wand", Material.WOOD_AXE, (byte) 0)
                    || UtilInv.IsItem(e.getItemDrop().getItemStack(), C.cAqua + C.Bold + "Previous Page", Material.PAPER, (byte) 0)
                    || UtilInv.IsItem(e.getItemDrop().getItemStack(), C.cAqua + C.Bold + "Next Page", Material.PAPER, (byte) 0)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (UtilEvent.isAction(e, ActionType.R)) {
            ItemStack item = e.getItem();
            if (item != null) {
                Player player = e.getPlayer();
                if (isBuilderMode(player)) {
                    if (item.hasItemMeta()) {
                        BuilderPlayer bPlayer = builders.get(player.getName());
                        if (item.getItemMeta().getDisplayName().equals(C.cAqua + C.Bold + "Previous Page")) {
                            bPlayer.flipPage(bPlayer.getCurrentPage() - 1);
                        } else if (item.getItemMeta().getDisplayName().equals(C.cAqua + C.Bold + "Next Page")) {
                            bPlayer.flipPage(bPlayer.getCurrentPage() + 1);
                        }
                    }
                }
            }
        }
    }
}