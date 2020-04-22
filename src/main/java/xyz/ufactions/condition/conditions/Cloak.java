package xyz.ufactions.condition.conditions;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xyz.ufactions.condition.Condition;
import xyz.ufactions.condition.ConditionManager;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.visibility.VisibilityManager;

public class Cloak extends Condition {

    public Cloak(ConditionManager manager, String reason, LivingEntity ent, LivingEntity source, ConditionType type, int multi, int ticks, boolean add, Material visualType, byte visualData, boolean showIndicator) {
        super(manager, reason, ent, source, type, multi, ticks, add, visualType, visualData, showIndicator, false);

        informOn = "Turning on cloaking capabilities.";
        informOff = "Turning off cloaking.";
    }

    @Override
    public void add() {
        if (!(ent instanceof Player)) return;

        VisibilityManager.instance.setVisibility((Player) ent, false, UtilServer.getPlayers());

        for (Entity entity : ent.getWorld().getEntities()) {
            if (!(ent instanceof Creature)) continue;

            Creature creature = (Creature) ent;

            if (creature.getTarget() != null && !creature.getTarget().equals(ent)) continue;

            creature.setTarget(null);
        }
    }

    @Override
    public void remove() {
        VisibilityManager.instance.setVisibility((Player) ent, true, UtilServer.getPlayers());
    }
}