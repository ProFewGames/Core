package xyz.ufactions.condition;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import xyz.ufactions.damage.CustomDamageEvent;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;
import xyz.ufactions.visibility.VisibilityManager;

public class ConditionEffect implements Listener {

    protected ConditionManager manager;

    public ConditionEffect(ConditionManager manager) {
        this.manager = manager;

        manager.getPlugin().getServer().getPluginManager().registerEvents(this, manager.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void cloak(CustomDamageEvent e) {
        if (e.IsCancelled()) return;

        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        LivingEntity ent = e.getDamageeEntity();
        if (ent == null) return;

        if (!manager.isCloaked(ent)) return;

        //Set Damage
        e.setCancelled("Cloak");
    }

    @EventHandler
    public void cloak(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        for (LivingEntity ent : manager.getActiveConditions().keySet()) {
            if (!(ent instanceof Player)) continue;

            Player player = (Player) ent;

            //Hide
            if (manager.isCloaked(ent)) {
                for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                    VisibilityManager.instance.setVisibility(player, false, other);
                }
            }
            // Show
            else {
                for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                    VisibilityManager.instance.setVisibility(player, true, other);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cloak(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof Player)) return;

        if (!manager.hasCondition((LivingEntity) e.getTarget(), Condition.ConditionType.CLOAK, null)) return;

        e.setCancelled(true);
    }
}