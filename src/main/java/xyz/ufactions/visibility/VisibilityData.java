package xyz.ufactions.visibility;

import org.bukkit.entity.Player;
import xyz.ufactions.libs.NautHashMap;
import xyz.ufactions.recharge.Recharge;
import xyz.ufactions.timings.TimingManager;

import java.util.Iterator;

/**
 * This class is meant to force show/hide methods because
 * of plugin reloads and current instability issues
 */
public class VisibilityData {

    private NautHashMap<Player, Boolean> shouldHide = new NautHashMap<>();

    /**
     * @param player The player who we're [@param hide] from {@param target}
     * @param target The target who is going into {@param target}
     * @param hide   true/false = hide/show = The target from player
     */
    public void updatePlayer(Player player, Player target, boolean hide) {
        TimingManager.startTotal("VisData updatePlayer");
        if (attemptToProcess(player, target, hide)) {
            shouldHide.remove(target);
        } else {
            shouldHide.put(target, hide);
        }
        TimingManager.stopTotal("VisData updatePlayer");
    }

    /**
     * @return If the {@param target} has been hidden/shown in the past 250ms the it will return false
     * else true; If false they're going to get put into a queue until their recharge is available.
     */
    private boolean attemptToProcess(Player player, Player target, boolean hide) {
        TimingManager.startTotal("VisData attemptToProcess");
        if (Recharge.Instance.use(player, "VIS " + target.getName(), 250, false, false)) {
            if (hide) {
                TimingManager.start("Hide Player");
                player.hidePlayer(target);
                TimingManager.stop("Hide Player");
            } else {
                TimingManager.start("Show Player");
                player.showPlayer(target);
                TimingManager.stop("Show Player");
            }
            return true;
        }
        TimingManager.stopTotal("VisData attemptToProcess");
        return false;
    }

    /**
     * Update the players visibility when their recharge was unavailable
     */
    public void attemptToProcessUpdate(Player player) {
        TimingManager.startTotal("VisData attemptToProcessUpdate shouldHide");
        if (!shouldHide.isEmpty()) {
            for (Iterator<Player> targetIter = shouldHide.keySet().iterator(); targetIter.hasNext(); ) {
                Player target = targetIter.next();
                boolean hide = shouldHide.get(target);

                if (!target.isOnline() || !target.isValid() || attemptToProcess(player, target, hide)) {
                    targetIter.remove();
                }
            }
        }
        TimingManager.stopTotal("VisData attemptToProcessUpdate shouldHide");
    }
}