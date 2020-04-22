package xyz.ufactions.combat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xyz.ufactions.damage.DamageChange;
import xyz.ufactions.libs.UtilTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CombatComponent {

    private boolean player = false;

    private LinkedList<CombatDamage> damage;

    protected String entityName;
    protected long lastDamage = 0;

    public CombatComponent(String name, LivingEntity ent) {
        this.entityName = name;
        if (ent != null) {
            if (ent instanceof Player) {
                this.player = true;
            }
        }
    }

    public void addDamage(String source, double dmg, List<DamageChange> mod) {
        if (source == null) source = "n/a";

        getDamage().addFirst(new CombatDamage(source, dmg, mod));
        lastDamage = System.currentTimeMillis();
    }

    public String getName() {
        if (entityName.equals("Null")) return "World";

        return entityName;
    }

    public LinkedList<CombatDamage> getDamage() {
        if (damage == null) damage = new LinkedList<>();

        return damage;
    }

    public String getReason() {
        if (damage.isEmpty()) return null;

        return damage.get(0).getName();
    }

    public long getLastDamage() {
        return lastDamage;
    }

    public int getTotalDamage() {
        int total = 0;
        for (CombatDamage cur : getDamage()) total += cur.getDamage();
        return total;
    }

    public String getBestWeapon() {
        HashMap<String, Integer> cumulative = new HashMap<>();
        String weapon = null;
        int best = 0;
        for (CombatDamage cur : damage) {
            int damage = 0;
            if (cumulative.containsKey(cur.getName())) damage = cumulative.get(cur.getName());

            cumulative.put(cur.getName(), damage);

            if (damage >= best)
                weapon = cur.getName();
        }

        return weapon;
    }

    public String display(long deathTime) {
        return "{CombatComponent$1: " +
                "EntityName=" + entityName +
                ",TotalDamage=" + getTotalDamage() +
                ",BestWeapon=" + getBestWeapon() +
                ",Time=" + UtilTime.convertString(deathTime - lastDamage, 1, UtilTime.TimeUnit.FIT) +
                "}"; // TODO : CHANGE
    }

    public String display(long deathTime, CombatDamage damage) {
        return "{CombatComponent$2: " +
                "EntityName=" + entityName +
                ",TotalDamage=" + getTotalDamage() +
                ",BestWeapon=" + getBestWeapon() +
                ",Time=" + UtilTime.convertString(deathTime - lastDamage, 1, UtilTime.TimeUnit.FIT) +
                ",Damage=" + damage +
                "}"; // TODO : CHANGE
    }

    public boolean isPlayer() {
        return player;
    }

    public String getLastDamageSource() {
        return damage.getFirst().getName();
    }
}