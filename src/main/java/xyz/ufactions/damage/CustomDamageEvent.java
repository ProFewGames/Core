package xyz.ufactions.damage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import xyz.ufactions.libs.C;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private EntityDamageEvent.DamageCause eventCause;
    private double initialDamage;

    private ArrayList<DamageChange> damageMult = new ArrayList<>();
    private ArrayList<DamageChange> damageMod = new ArrayList<>();

    private ArrayList<String> cancellers = new ArrayList<>();

    private HashMap<String, Double> knockbackMod = new HashMap<>();

    //Ents
    private LivingEntity damageeEntity;
    private Player damageePlayer;
    private LivingEntity damagerEntity;
    private Player damagerPlayer;
    private Projectile projectile;
    private Location knockbackOrigin = null;

    //Flags
    private boolean ignoreArmor = false;
    private boolean ignoreRate = false;
    private boolean knockback = true;
    private boolean damageeBrute = false;
    private boolean damageToLevel = true;

    public CustomDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile projectile, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String initialSource, String initialReason, boolean cancelled) {
        this.eventCause = cause;

//        if(initialSource==null&&initialReason==null)
        initialDamage = damage;

        damageeEntity = damagee;
        if (damageeEntity != null && damageeEntity instanceof Player) damageePlayer = (Player) damageeEntity;

        damagerEntity = damager;
        if (damagerEntity != null && damagerEntity instanceof Player) damagerPlayer = (Player) damagerEntity;

        this.projectile = projectile;

        this.knockback = knockback;
        this.ignoreRate = ignoreRate;
        this.ignoreArmor = ignoreArmor;

        if (initialSource != null && initialReason != null)
            addMod(initialSource, initialReason, 0, true);

        if (eventCause == EntityDamageEvent.DamageCause.FALL)
            this.ignoreArmor = true;

        if (cancelled) setCancelled("Pre-Cancelled");
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void addMult(String source, String reason, double mod, boolean useAttackName) {
        damageMult.add(new DamageChange(source, reason, mod, useAttackName));
    }

    public void addMod(String source, String reason, double mod, boolean useAttackName) {
        damageMod.add(new DamageChange(source, reason, mod, useAttackName));
    }

    public void addKnockback(String reason, double d) {
        knockbackMod.put(reason, d);
    }

    public boolean IsCancelled() {
        return !cancellers.isEmpty();
    }

    public void setCancelled(String reason) {
        cancellers.add(reason);
    }

    public double getDamage() {
        double damage = getInitialDamage();

        for (DamageChange mult : damageMod)
            damage += mult.getDamage();

        for (DamageChange mult : damageMult)
            damage *= mult.getDamage();

        return damage;
    }

    public LivingEntity getDamageeEntity() {
        return damageeEntity;
    }

    public Player getDamageePlayer() {
        return damageePlayer;
    }

    public LivingEntity getDamagerEntity(boolean ranged) {
        if (ranged) return damagerEntity;
        else if (projectile == null) return damagerEntity;
        return null;
    }

    public Player getDamagerPlayer(boolean ranged) {
        if (ranged) return damagerPlayer;
        else if (projectile == null) return damagerPlayer;
        return null;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return eventCause;
    }

    public double getInitialDamage() {
        return initialDamage;
    }

    public void setIgnoreArmor(boolean ignoreArmor) {
        this.ignoreArmor = ignoreArmor;
    }

    public void setIgnoreRate(boolean ignoreRate) {
        this.ignoreRate = ignoreRate;
    }

    public void setKnockback(boolean knockback) {
        this.knockback = knockback;
    }

    public void setBrute() {
        this.damageeBrute = true;
    }

    public boolean isBrute() {
        return damageeBrute;
    }

    public String getReason() {
        String reason = "";

        //Get Reason
        for (DamageChange change : damageMod)
            if (change.useReason())
                reason += C.mSkill + change.getReason() + ChatColor.GRAY + ", ";

        // Trim Reason
        if (reason.length() > 0) {
            reason = reason.substring(0, reason.length() - 2);
            return reason;
        }

        return null;
    }

    public boolean isKnockback() {
        return knockback;
    }

    public boolean ignoreRate() {
        return ignoreRate;
    }

    public boolean ignoreArmor() {
        return ignoreArmor;
    }

    public void setDamager(LivingEntity ent) {
        if (ent == null) return;

        damagerEntity = ent;

        damagerPlayer = null;
        if (ent instanceof Player) damagerPlayer = (Player) ent;
    }

    public void setDamagee(LivingEntity ent) {
        damageeEntity = ent;

        damageePlayer = null;
        if (ent instanceof Player)
            damageePlayer = (Player) ent;
    }

    public void changeReason(String initial, String reason) {
        for (DamageChange change : damageMod)
            if (change.getReason().equals(initial))
                change.setReason(reason);
    }

    public void setKnockbackOrigin(Location loc) {
        this.knockbackOrigin = loc;
    }

    public Location getKnockbackOrigin() {
        return knockbackOrigin;
    }

    public ArrayList<DamageChange> getDamageMod() {
        return damageMod;
    }

    public ArrayList<DamageChange> getDamageMult() {
        return damageMult;
    }

    public HashMap<String, Double> getKnockback() {
        return knockbackMod;
    }

    public ArrayList<String> getCancellers() {
        return cancellers;
    }

    public void setDamageToLevel(boolean damageToLevel) {
        this.damageToLevel = damageToLevel;
    }

    public boolean displayDamageToLevel() {
        return damageToLevel;
    }

    @Override
    public boolean isCancelled() {
        return IsCancelled();
    }

    @Override
    @Deprecated
    /**
     * Don't call this method. Use setCancelled(String) instead.
     */
    public void setCancelled(boolean b) {
        setCancelled("THERE'S A SNAKE IN MY BOOT");
    }
}