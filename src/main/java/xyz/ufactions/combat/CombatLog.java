package xyz.ufactions.combat;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import xyz.ufactions.damage.DamageChange;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CombatLog {

    private LinkedList<CombatComponent> damager = new LinkedList<>();
    private CombatComponent player;
    private long expireTime;

    private long deathTime = 0;
    private CombatComponent killer;
    private int assistants;

    private String killedColor = ChatColor.DARK_GRAY + "";
    private String killerColor = ChatColor.DARK_RED + "";

    protected CombatComponent lastDamager;
    protected long lastDamaged;
    protected long lastCombat;

    public CombatLog(Player player, long expireTime) {
        this.expireTime = expireTime;
        this.player = new CombatComponent(player.getName(), player);
    }

    public LinkedList<CombatComponent> getAttackers() {
        return damager;
    }

    public CombatComponent getPlayer() {
        return player;
    }

    public void attacked(String damagerName, double damage, LivingEntity damagerEnt, String attackName, List<DamageChange> mod) {
        //Add Attack
        CombatComponent comp = getEnemy(damagerName, damagerEnt);

        comp.addDamage(attackName, damage, mod);

        //Set Last
        lastDamager = comp;
        lastDamaged = System.currentTimeMillis();
        lastCombat = System.currentTimeMillis();
    }

    public CombatComponent getEnemy(String name, LivingEntity ent) {
        expireOld();

        CombatComponent component = null;
        for (CombatComponent cur : damager) {
            if (cur.getName().equals(name)) component = cur;
        }

        //Player has attacked in past
        if (component != null) {
            damager.remove(component);
            damager.addFirst(component);
            return damager.getFirst();
        }

        damager.addFirst(new CombatComponent(name, ent));
        return damager.getFirst();
    }

    public void expireOld() {
        int expireFrom = -1;
        for (int i = 0; i < damager.size(); i++) {
            if (UtilTime.elapsed(damager.get(i).getLastDamage(), expireTime)) {
                expireFrom = i;
                break;
            }
        }

        if (expireFrom != -1)
            while (damager.size() > expireFrom) damager.remove(expireFrom);
    }

    public LinkedList<String> display() {
        LinkedList<String> out = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            if (i < damager.size())
                out.add("{CombatLog$1: " +
                        "CombatId=" + i +
                        "CombatComponent=" + damager.get(i).display(deathTime) +
                        "}");
        }
        return out;
    }

    public LinkedList<String> displayAbsolute() {
        HashMap<Long, String> components = new HashMap<>();
        for (CombatComponent cur : damager) {
            for (CombatDamage damage : cur.getDamage()) {
                components.put(damage.getTime(), cur.display(deathTime, damage));
            }
        }

        int id = components.size();
        LinkedList<String> out = new LinkedList<>();

        while (!components.isEmpty()) {
            long bestTime = 0;
            String bestString = null;

            for (long time : components.keySet()) {
                if (time > bestTime || bestString == null) {
                    bestTime = time;
                    bestString = components.get(time);
                }
            }

            components.remove(bestTime);

            out.addFirst("{CombatLog$2: " +
                    "CombatId=" + id +
                    "CombatComponent=" + bestString +
                    "}");
            id--;
        }

        return out;
    }

    public CombatComponent getKiller() {
        return killer;
    }

    public void setKiller(CombatComponent killer) {
        this.killer = killer;
    }

    public int getAssists() {
        return assistants;
    }

    public void setAssists(int assistants) {
        this.assistants = assistants;
    }

    public CombatComponent getLastDamager() {
        return lastDamager;
    }

    public long getLastDamaged() {
        return lastDamaged;
    }

    public long getLastCombat() {
        return lastCombat;
    }

    public void setLastCombat(long lastCombat) {
        this.lastCombat = lastCombat;
    }

    public long getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(long deathTime) {
        this.deathTime = deathTime;
    }

    public String getKilledColor() {
        return killedColor;
    }

    public void setKilledColor(String killedColor) {
        this.killedColor = killedColor;
    }

    public String getKillerColor() {
        return killerColor;
    }

    public void setKillerColor(String killerColor) {
        this.killerColor = killerColor;
    }
}