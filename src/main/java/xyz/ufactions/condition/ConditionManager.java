package xyz.ufactions.condition;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.condition.events.ConditionApplyEvent;
import xyz.ufactions.libs.*;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.*;

/**
 * We've changed 'Condition' to 'Player Mood'
 */
public class ConditionManager extends Module {

    private ConditionFactory factory;
    private ConditionApplicator applicator;
    protected ConditionEffect effect;

    private WeakHashMap<LivingEntity, LinkedList<Condition>> conditions = new WeakHashMap<>();
    private WeakHashMap<LivingEntity, LinkedList<ConditionActive>> activeConditions = new WeakHashMap<>();

    private HashSet<Entity> items = new HashSet<>();

    public ConditionManager(JavaPlugin plugin) {
        super("Player Mood Manager", plugin);

        factory();
        applicator();
        effect();
    }

    public ConditionFactory factory() {
        if (factory == null) factory = new ConditionFactory(this);
        return factory;
    }

    public ConditionApplicator applicator() {
        if (applicator == null) applicator = new ConditionApplicator();
        return applicator;
    }

    public ConditionEffect effect() {
        if (effect == null) effect = new ConditionEffect(this);
        return effect;
    }

    public Condition addCondition(Condition condition) {
        //Event
        ConditionApplyEvent event = new ConditionApplyEvent(condition);
        getPlugin().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return null;

        //Add Condition
        if (!conditions.containsKey(condition.getEnt())) conditions.put(condition.getEnt(), new LinkedList<>());

        conditions.get(condition.getEnt()).add(condition);

        //Condition Add
        condition.onConditionAdd();

        //Indicator
        handleIndicator(condition);

        return condition;
    }

    public void handleIndicator(Condition condition) {
        ConditionActive ind = getIndicatorType(condition);

        //New Condition
        if (ind == null) {
            addIndicator(condition);
        }
        //Condition Exists
        else {
            updateActive(ind, condition);
        }
    }

    public ConditionActive getIndicatorType(Condition condition) {
        if (!activeConditions.containsKey(condition.getEnt()))
            activeConditions.put(condition.getEnt(), new LinkedList<>());

        for (ConditionActive ind : activeConditions.get(condition.getEnt()))
            if (ind.getCondition().getType() == condition.getType())
                return ind;

        return null;
    }

    public void addIndicator(Condition condition) {
        //Create
        ConditionActive newInd = new ConditionActive(condition);

        ///Get Inds
        if (!activeConditions.containsKey(condition.getEnt()))
            activeConditions.put(condition.getEnt(), new LinkedList<>());

        LinkedList<ConditionActive> entInds = activeConditions.get(condition.getEnt());

        //Add
        entInds.addFirst(newInd);

        //Inform
        if (condition.getInformOn() != null)
            UtilPlayer.message(condition.getEnt(), F.main("Player Mood", condition.getInformOn()));
    }

    public void updateActive(ConditionActive active, Condition condition) {
        //Non Additive
        if (!active.getCondition().isExpired())
            if (active.getCondition().isBetterOrEqual(condition, condition.isAdd())) return;

        active.setCondition(condition);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void expireConditions(UpdateEvent e) {
        if (e.getType() != UpdateType.TICK) return;

        /** Conditions **/
        for (LivingEntity ent : conditions.keySet()) {
            Iterator<Condition> conditionIterator = conditions.get(ent).iterator();
            while (conditionIterator.hasNext()) {
                Condition cond = conditionIterator.next();
                if (cond.tick()) conditionIterator.remove();
            }
        }

        /** Indicators **/
        for (LivingEntity ent : activeConditions.keySet()) {
            Iterator<ConditionActive> conditionIndicationIterator = activeConditions.get(ent).iterator();
            while (conditionIndicationIterator.hasNext()) {
                ConditionActive conditionIndicator = conditionIndicationIterator.next();
                if (conditionIndicator.getCondition().isExpired()) {
                    Condition replacement = getBestCondition(ent, conditionIndicator.getCondition().getType());
                    if (replacement == null) {
                        conditionIndicationIterator.remove();

                        //Inform
                        if (conditionIndicator.getCondition().getInformOff() != null)
                            UtilPlayer.message(conditionIndicator.getCondition().getEnt(), F.main("Player Mood", conditionIndicator.getCondition().getInformOff()));
                    } else
                        updateActive(conditionIndicator, replacement);
                }
            }
        }
    }

    public Condition getBestCondition(LivingEntity ent, Condition.ConditionType type) {
        if (!conditions.containsKey(ent)) return null;

        Condition best = null;

        for (Condition con : conditions.get(ent)) {
            if (con.getType() != type) continue;

            if (con.isExpired()) continue;

            if (best == null) {
                best = con;
                continue;
            }
            if (con.isBetterOrEqual(best, false))
                best = con;
        }
        return best;
    }

    public Condition getActiveCondition(LivingEntity ent, Condition.ConditionType type) {
        if (!activeConditions.containsKey(ent)) return null;

        for (ConditionActive ind : activeConditions.get(ent)) {
            if (ind.getCondition().getType() != type) continue;
            if (ind.getCondition().isExpired()) continue;
            return ind.getCondition();
        }
        return null;
    }

    @EventHandler
    public void remove(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK) return;

        HashSet<Entity> expired = new HashSet<>();

        for (Entity cur : items)
            if (UtilEnt.isGrounded(cur) || cur.isDead() || !cur.isValid()) expired.add(cur);

        for (Entity cur : expired) {
            items.remove(cur);
            cur.remove();
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        clean(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        clean(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void death(EntityDeathEvent e) {
        //Still Alive - SHOULD IGNORE DEATHS FROM DOMINATE
        if (e.getEntity() instanceof Player)
            if (e.getEntity().getHealth() > 0) return;

        clean(e.getEntity());
    }

    public void clean(LivingEntity ent) {
        //Wipe Conditions
        conditions.remove(ent);
        activeConditions.remove(ent);
    }

    @EventHandler
    public void debug(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC) return;

        for (LivingEntity ent : activeConditions.keySet()) {
            if (!(ent instanceof Player)) continue;

            Player player = (Player) ent;
            if (player.getItemInHand() == null) continue;

            if (player.getItemInHand().getType() != Material.PAPER) continue;

            if (!player.isOp()) continue;

            UtilPlayer.message(player, C.cGray + activeConditions.get(ent).size() + " Indicators ----------- " + conditions.get(ent).size() + " Player Moods");
            for (ConditionActive ind : activeConditions.get(ent))
                UtilPlayer.message(player,
                        F.elem(ind.getCondition().getType() + " " + (ind.getCondition().getMult() + 1)) + " for " +
                                F.time(UtilTime.convertString(ind.getCondition().getTicks() * 50L, 1, UtilTime.TimeUnit.FIT)) + " via " +
                                F.skill(ind.getCondition().getReason()) + " from " +
                                F.name(UtilEnt.getName(ind.getCondition().getSource())) + ".");
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        if (e.isCancelled()) return;

        if (items.contains(e.getItem())) e.setCancelled(true);
    }

    @EventHandler
    public void hoppedPickup(InventoryPickupItemEvent e) {
        if (e.isCancelled()) return;

        if (items.contains(e.getItem())) e.setCancelled(true);
    }

    public void endCondition(LivingEntity target, Condition.ConditionType type, String reason) {
        if (!conditions.containsKey(target)) return;

        for (Condition cond : conditions.get(target))
            if (reason == null || cond.getReason().equals(reason))
                if (type == null || cond.getType() == type) {
                    cond.expire();

                    Condition best = getBestCondition(target, cond.getType());
                    if (best != null) best.apply();
                }
    }

    public boolean hasCondition(LivingEntity target, Condition.ConditionType type, String reason) {
        if (!conditions.containsKey(target)) return false;

        for (Condition condition : conditions.get(target))
            if (reason == null || condition.getReason().equals(reason))
                if (type == null || condition.getType() == type) return true;

        return false;
    }

    public WeakHashMap<LivingEntity, LinkedList<ConditionActive>> getActiveConditions() {
        return activeConditions;
    }

    public boolean isCloaked(LivingEntity ent) {
        if (!activeConditions.containsKey(ent)) return false;

        for (ConditionActive ind : activeConditions.get(ent))
            if (ind.getCondition().getType() == Condition.ConditionType.CLOAK)
                if (!ind.getCondition().isExpired()) return true;

        return false;
    }

    @EventHandler
    public void cleanUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        Iterator<Map.Entry<LivingEntity, LinkedList<ConditionActive>>> conditionIndIterator = activeConditions.entrySet().iterator();

        while (conditionIndIterator.hasNext()) {
            Map.Entry<LivingEntity, LinkedList<ConditionActive>> entry = conditionIndIterator.next();
            LivingEntity ent = entry.getKey();

            if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player) ent).isOnline())) {
                ent.remove();
                conditionIndIterator.remove();
            }
        }

        Iterator<Map.Entry<LivingEntity, LinkedList<Condition>>> conditionIterator = conditions.entrySet().iterator();

        while (conditionIterator.hasNext()) {
            Map.Entry<LivingEntity, LinkedList<Condition>> entry = conditionIterator.next();
            LivingEntity ent = entry.getKey();

            if (ent.isDead() || !ent.isValid() || (ent instanceof Player && !((Player) ent).isOnline())) {
                ent.remove();
                conditionIndIterator.remove();
            }
        }
    }
}