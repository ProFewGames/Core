package xyz.ufactions.monitor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.HashSet;

public class LagMeter extends Module {

    private HashSet<Player> monitoring = new HashSet<>();

    private double tps;
    private double tpsAverage;
    private int count;
    private long lastRun = -1;
    private long lastAverage;

    public LagMeter(JavaPlugin plugin) {
        super("Lag Meter", plugin);

        lastRun = System.currentTimeMillis();
        lastAverage = System.currentTimeMillis();
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("core.command.monitor")) {
            if (e.getMessage().trim().equalsIgnoreCase("/monitor")) {
                if (monitoring.contains(e.getPlayer())) monitoring.remove(e.getPlayer());
                else monitoring.add(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        monitoring.remove(e.getPlayer());
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC) return;

        long now = System.currentTimeMillis();
        tps = 1000D / (now - lastRun) * 20D;

        sendUpdates();

        if (count % 30 == 0) {
            tpsAverage = 30000D / (now - lastAverage) * 20D;
            lastAverage = now;
        }

        lastRun = now;

        count++;
    }

    public double getTPS() {
        return tps;
    }

    public double getAverageTPS() {
        return tpsAverage;
    }

    private void sendUpdates() {
        for (Player player : monitoring) {
            sendUpdate(player);
        }
    }

    private void sendUpdate(Player player) {
        UtilPlayer.message(player, C.mHead + C.Strike + "------------------------------");
        UtilPlayer.message(player, C.mHead + "TPS: " + C.mBody + tps);
        UtilPlayer.message(player, C.mHead + "Average TPS: " + C.mBody + tpsAverage);
        UtilPlayer.message(player, C.mHead + "Max Memory: " + C.mBody + Runtime.getRuntime().maxMemory() / 1024L / 1024L);
        UtilPlayer.message(player, C.mHead + "Allocated Memory: " + C.mBody + Runtime.getRuntime().totalMemory() / 1024L / 1024L);
        UtilPlayer.message(player, C.mHead + "Free Memory: " + C.mBody + Runtime.getRuntime().freeMemory() / 1024L / 1024L);
        UtilPlayer.message(player, C.mHead + C.Strike + "------------------------------");
    }
}