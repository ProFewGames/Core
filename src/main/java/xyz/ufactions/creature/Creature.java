package xyz.ufactions.creature;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.ufactions.api.Module;
import xyz.ufactions.creature.commands.MobCommand;
import xyz.ufactions.libs.UtilAction;
import xyz.ufactions.libs.UtilAlg;
import xyz.ufactions.libs.UtilPlayer;

import java.util.HashMap;

public class Creature extends Module {

    private boolean disableCustom = false;

    public Creature(JavaPlugin plugin) {
        super("Creature", plugin);
    }

    @Override
    public void addCommands() {
        addCommand(new MobCommand(this));
    }

    public Entity spawnEntity(Location location, EntityType entityType) {
        return location.getWorld().spawnEntity(location, entityType);
    }

    @EventHandler
    public void customCreeperExplode(EntityExplodeEvent e) {
        if (disableCustom) return;

        if (!(e.getEntity() instanceof Creeper)) return;

        HashMap<Player, Double> players = UtilPlayer.getInRadius(e.getEntity().getLocation(), 8d);
        for (Player cur : players.keySet()) {
            Vector vec = UtilAlg.getTrajectory(e.getEntity().getLocation(), cur.getLocation());
            UtilAction.velocity(cur, vec, 1 + 2 * players.get(cur), false, 0, 0.5 + 1 * (players.get(cur)), 2, true);
        }
    }

    public void setDisableCustom(boolean disableCustom) {
        this.disableCustom = disableCustom;
    }
}