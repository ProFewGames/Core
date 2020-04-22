package xyz.ufactions.combat;

import org.bukkit.entity.LivingEntity;
import xyz.ufactions.libs.UtilTime;

import java.util.LinkedList;
import java.util.WeakHashMap;

public class ClientCombat {

    private LinkedList<CombatLog> kills = new LinkedList<>();
    private LinkedList<CombatLog> assists = new LinkedList<>();
    private LinkedList<CombatLog> deaths = new LinkedList<>();

    private WeakHashMap<LivingEntity, Long> lastHurt = new WeakHashMap<>();
    private WeakHashMap<LivingEntity, Long> lastHurtBy = new WeakHashMap<>();
    private long lastHurtByWorld = 0;

    public LinkedList<CombatLog> getKills() {
        return kills;
    }

    public LinkedList<CombatLog> getAssists() {
        return assists;
    }

    public LinkedList<CombatLog> getDeaths() {
        return deaths;
    }

    public boolean canBeHurtBy(LivingEntity damager) {
        if (damager == null) {
            if (UtilTime.elapsed(lastHurtByWorld, 250)) {
                lastHurtByWorld = System.currentTimeMillis();
                return true;
            } else {
                return false;
            }
        }

        if (!lastHurtBy.containsKey(damager)) {
            lastHurtBy.put(damager, System.currentTimeMillis());
            return true;
        }
        if (System.currentTimeMillis() - lastHurtBy.get(damager) > 400) {
            lastHurtBy.put(damager, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public boolean canHurt(LivingEntity damagee) {
        if (damagee == null) return true;

        if (!lastHurt.containsKey(damagee)) {
            lastHurt.put(damagee, System.currentTimeMillis());
            return true;
        }
        if (System.currentTimeMillis() - lastHurt.get(damagee) > 400) {
            lastHurt.put(damagee, System.currentTimeMillis());
            return true;
        }
        return false;
    }
}