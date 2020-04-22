package xyz.ufactions.combat;

import xyz.ufactions.damage.DamageChange;

import java.util.ArrayList;
import java.util.List;

public class CombatDamage {

    private String name;
    private double damage;
    private long time;
    private List<DamageChange> mod = new ArrayList<>();

    public CombatDamage(String name, double dmg, List<DamageChange> mod) {
        this.name = name;
        this.damage = dmg;
        this.time = System.currentTimeMillis();
        this.mod = mod;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public long getTime() {
        return time;
    }

    public List<DamageChange> getDamageMod() {
        return mod;
    }
}