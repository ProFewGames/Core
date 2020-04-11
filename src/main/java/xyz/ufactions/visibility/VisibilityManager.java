package xyz.ufactions.visibility;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.NautHashMap;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.timings.TimingManager;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.Iterator;

public class VisibilityManager extends Module {

    public static VisibilityManager instance;

    private NautHashMap<Player, VisibilityData> data = new NautHashMap<>();

    protected VisibilityManager(JavaPlugin plugin) {
        super("Visibility Manager", plugin);
    }

    public VisibilityData getDataFor(Player player) {
        if (!data.containsKey(player))
            data.put(player, new VisibilityData());
        return data.get(player);
    }

    public void setVisibility(Player target, boolean isVisible, Player... viewers) {
        TimingManager.startTotal("VisMan setVis");
        for (Player player : viewers) {
            if (player.equals(target)) continue;
            getDataFor(player).updatePlayer(player, target, !isVisible);
        }
        TimingManager.stopTotal("VisMan setVis");
    }

    public void refreshPlayerToAll(Player player) {
        setVisibility(player, false, UtilServer.getPlayers());
        setVisibility(player, true, UtilServer.getPlayers());
    }

    @EventHandler
    public void update(UpdateEvent e) {
        if (e.getType() != UpdateType.TICK) return;

        TimingManager.startTotal("VisMan update");
        Iterator<Player> playerIter = data.keySet().iterator();
        while (playerIter.hasNext()) {
            Player player = playerIter.next();
            if (!player.isOnline() || !player.isValid()) {
                playerIter.remove();
                continue;
            }
            data.get(player).attemptToProcessUpdate(player);
        }
        TimingManager.stopTotal("VisMan update");
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        data.remove(e.getPlayer());
    }
}