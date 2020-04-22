package xyz.ufactions.damage;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.ufactions.api.Module;
import xyz.ufactions.combat.CombatManager;
import xyz.ufactions.condition.ConditionManager;
import xyz.ufactions.libs.*;

import java.util.Map;

public class DamageManager extends Module {

    private CombatManager combatManager;
    private ConditionManager conditionManager;

    public boolean useSimpleWeaponDamage = false;
    public boolean disableDamageChanges = false;

    private boolean enabled = true;

    public DamageManager(JavaPlugin plugin, CombatManager combatManager, ConditionManager conditionManager) {
        super("Damage Manager", plugin);

        this.combatManager = combatManager;
        this.conditionManager = conditionManager;
    }

    @EventHandler
    public void startDamageEvent(EntityDamageEvent e) {
        if (!enabled) return;

        boolean preCancel = false;
        if (e.isCancelled()) preCancel = true;

        if (!(e.getEntity() instanceof LivingEntity)) return;

        //Get Data
        LivingEntity damagee = getDamageeEntity(e);
        LivingEntity damager = UtilEvent.GetDamagerEntity(e, true);
        Projectile projectile = getProjectile(e);

        if (projectile instanceof Fish) return;

        //Pre-Event Modifications
        if (disableDamageChanges) weaponDamage(e, damager);

        double damage = e.getDamage();

        //Consistent Arrow Damage
        if (projectile != null && projectile instanceof Arrow) {
            damage = projectile.getVelocity().length() * 3;
        }

        //New Event
        newDamageEvent(damagee, damager, projectile, e.getCause(), damage, true, false, false, null, null, preCancel);

        e.setCancelled(true);
    }

    @EventHandler
    public void removeDemArrowsCrazyMan(EntityDamageEvent e) {
        if (e.isCancelled()) {
            Projectile projectile = getProjectile(e);

            if (projectile instanceof Arrow) {
                projectile.teleport(new Location(projectile.getWorld(), 0, 0, 0));
                projectile.remove();
            }
        }
    }

    public void newDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile projectile, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason) {
        newDamageEvent(damagee, damager, projectile, cause, damage, knockback, ignoreRate, ignoreArmor, source, reason, false);
    }

    public void newDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile projectile, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason, boolean cancelled) {
        getPlugin().getServer().getPluginManager().callEvent(new CustomDamageEvent(damagee, damager, projectile, cause, damage, knockback, ignoreRate, ignoreArmor, source, reason, cancelled));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void cancelDamageEvent(CustomDamageEvent e) {
        if (e.getDamageeEntity().getHealth() <= 0) {
            e.setCancelled("0 Health");
            return;
        }
        if (e.getDamageePlayer() != null) {
            Player damagee = e.getDamageePlayer();

            // Not Survival
            if (damagee.getGameMode() == GameMode.CREATIVE) {
                e.setCancelled("Damagee in Creative");
                return;
            } else if (damagee.getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled("Damagee in Spectator");
                return;
            }

            //Limit Mob/World Damage Rate
            if (!e.ignoreRate()) {
                if (!combatManager.get(damagee.getName()).canBeHurtBy(e.getDamagerEntity(true))) {
                    e.setCancelled("World/Monster Damage Rate");
                    return;
                }
            }
        }

        if (e.getDamagerPlayer(true) != null) {
            Player damager = e.getDamagerPlayer(true);

            //Not Survival
            if (damager.getGameMode() != GameMode.SURVIVAL) {
                e.setCancelled("Damager in Creative");
                return;
            }

            //Damage Rate
            if (!e.ignoreRate())
                if (!combatManager.get(damager.getName()).canHurt(e.getDamageeEntity())) {
                    e.setCancelled("PvP Damage Rate");
                    return;
                }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void handleEnchants(CustomDamageEvent e) {
        if (e.isCancelled()) return;

        //Defensive
        Player damagee = e.getDamageePlayer();
        if (damagee != null) {
            for (ItemStack stack : damagee.getInventory().getArmorContents()) {
                if (stack == null) continue;

                Map<Enchantment, Integer> enchants = stack.getEnchantments();
                for (Enchantment ench : enchants.keySet()) {
                    if (ench.equals(Enchantment.PROTECTION_ENVIRONMENTAL))
                        e.addMod("Ench Prot", damagee.getName(), 0.5 * (double) enchants.get(ench), false);
                    else if (ench.equals(Enchantment.PROTECTION_FIRE) &&
                            e.getCause() == EntityDamageEvent.DamageCause.FIRE &&
                            e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK &&
                            e.getCause() == EntityDamageEvent.DamageCause.LAVA)
                        e.addMod("Ench Prot", damagee.getName(), 0.5 * (double) enchants.get(ench), false);
                    else if (ench.equals(Enchantment.PROTECTION_FALL) &&
                            e.getCause() == EntityDamageEvent.DamageCause.FALL)
                        e.addMod("Ench Prot", damagee.getName(), 0.5 * (double) enchants.get(ench), false);
                    else if (ench.equals(Enchantment.PROTECTION_EXPLOSIONS) &&
                            e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                        e.addMod("Ench Prot", damagee.getName(), 0.5 * (double) enchants.get(ench), false);
                    else if (ench.equals(Enchantment.PROTECTION_PROJECTILE) &&
                            e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
                        e.addMod("Ench Prot", damagee.getName(), 0.5 * (double) enchants.get(ench), false);
                }
            }
        }

        //Offensive
        Player damager = e.getDamagerPlayer(true);
        if (damager != null) {
            ItemStack stack = damager.getItemInHand();
            if (stack == null)
                return;

            Map<Enchantment, Integer> enchants = stack.getEnchantments();
            for (Enchantment ench : enchants.keySet()) {
                if (e.equals(Enchantment.ARROW_KNOCKBACK) || e.equals(Enchantment.KNOCKBACK))
                    e.addKnockback("Ench Knockback", 1 + (0.5 * (double) enchants.get(e)));

                else if (e.equals(Enchantment.ARROW_DAMAGE))
                    e.addMod("Enchant", "Ench Damage", 0.5 * (double) enchants.get(e), true);

                else if (e.equals(Enchantment.ARROW_FIRE) || e.equals(Enchantment.FIRE_ASPECT)) {
//                    if (conditionManager != null)
//                        conditionManager.factory().ignite("Ench Fire", e.getDamageeEntity(), damager,
//                                1 * (double)enchants.get(e), false, false); TODO : ADD IGNITE
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void endDamageEvent(CustomDamageEvent e) {
        if (!e.isCancelled() && e.getDamage() > 0) {
            damage(e);

            //DING ARROW
            if (e.getProjectile() != null && e.getProjectile() instanceof Arrow) {
                Player player = e.getDamagerPlayer(true);
                if (player != null) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);
                }
            }
        }
        displayDamage(e);
    }

    private void damage(CustomDamageEvent e) {
        if (e.getDamageeEntity() == null) return;

        if (e.getDamageeEntity().getHealth() <= 0) return;

        //Player Conditions
        if (e.getDamageePlayer() != null) {
            // Register Damage (must happen before damage)
            combatManager.addAttack(e);
        }
        if (e.getDamagerPlayer(true) != null && e.displayDamageToLevel()) {
            // Display Damage to Damager
            if (e.getCause() != EntityDamageEvent.DamageCause.THORNS) {
                e.getDamagerPlayer(true).setLevel((int) e.getDamage());
                TitleAPI.sendActionBar(e.getDamagerPlayer(true), C.mHead
                        + "Damage: " + C.mBody + (int) e.getDamage() + C.mHead + " hp");
            }
        }

        double bruteBonus = 0;
        if (e.isBrute() && (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || e.getCause() == EntityDamageEvent.DamageCause.CUSTOM))
            bruteBonus = Math.min(8, e.getDamage() * 2);

        // Do Damage
        handleDamage(e.getDamageeEntity(), e.getDamagerEntity(true), e.getCause(), (float) (e.getDamage() + bruteBonus), e.ignoreArmor());

        //Effect
        e.getDamageeEntity().playEffect(EntityEffect.HURT);

        //Sticky Arrow
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
            ((CraftLivingEntity) e.getDamageeEntity()).getHandle().o(((CraftLivingEntity) e.getDamageeEntity()).getHandle().bv() + 1);

        //Knockback
        if (e.isKnockback() && e.getDamagerEntity(true) != null) {
            //Base
            double knockback = e.getDamage();
            if (knockback < 2) knockback = 2;
            knockback = Math.log10(knockback);

            //Multis
            for (double cur : e.getKnockback().values()) knockback = knockback * cur;

            //Origin
            Location origin = e.getDamagerEntity(true).getLocation();
            if (e.getKnockbackOrigin() != null) origin = e.getKnockbackOrigin();

            //Vec
            Vector trajectory = UtilAlg.getTrajectory2d(origin, e.getDamageeEntity().getLocation());
            trajectory.multiply(0.6 * knockback);
            trajectory.setY(Math.abs(trajectory.getY()));

            //Debug
            if (e.getDamageeEntity() instanceof Player && UtilGear.isMat(((Player) e.getDamageeEntity()).getItemInHand(), Material.SUGAR)) {
                Bukkit.broadcastMessage("--------- " +
                        UtilEnt.getName(e.getDamageeEntity()) + " hurt by " + UtilEnt.getName(e.getDamagerEntity(true)) + "-----------");

                Bukkit.broadcastMessage(F.main("Debug", "Damage: " + e.getDamage()));
            }

            //Apply
            double vel = 0.2 + trajectory.length() * 0.8;

            UtilAction.velocity(e.getDamageeEntity(), trajectory, vel, false, 0, Math.abs(0.2 * knockback), 0.4 + (0.04 * knockback), true);
        }
    }

//    @EventHandler
//    public void debugVel2(PlayerVelocityEvent e) {
//        if (UtilGear.isMat(((Player) e.getPlayer()).getItemInHand(), Material.SUGAR)) {
//            Bukkit.broadcastMessage(F.main("Debug", "Event: " + e.getVelocity().length()));
//        }
//    }

    private void displayDamage(CustomDamageEvent event) {
        for (Player player : UtilServer.getPlayers()) {
            if (!UtilGear.isMat(player.getItemInHand(), Material.COMMAND))
                continue;

            UtilPlayer.message(player, " ");
            UtilPlayer.message(player, "=====================================");
            UtilPlayer.message(player, F.elem("Reason ") + event.getReason());
            UtilPlayer.message(player, F.elem("Cause ") + event.getCause());
            UtilPlayer.message(player, F.elem("Damager ") + UtilEnt.getName(event.getDamagerEntity(true)));
            UtilPlayer.message(player, F.elem("Damagee ") + UtilEnt.getName(event.getDamageeEntity()));
            UtilPlayer.message(player, F.elem("Projectile ") + UtilEnt.getName(event.getProjectile()));
            UtilPlayer.message(player, F.elem("Damage ") + event.getDamage());
            UtilPlayer.message(player, F.elem("Damage Initial ") + event.getInitialDamage());
            for (DamageChange cur : event.getDamageMod())
                UtilPlayer.message(player, F.elem("Mod ") + cur.getDamage() + " - " + cur.getReason() + " by " + cur.getSource());

            for (DamageChange cur : event.getDamageMult())
                UtilPlayer.message(player, F.elem("Mult ") + cur.getDamage() + " - " + cur.getReason() + " by " + cur.getSource());

            for (String cur : event.getKnockback().keySet())
                UtilPlayer.message(player, F.elem("Knockback ") + cur + " = " + event.getKnockback().get(cur));

            for (String cur : event.getCancellers())
                UtilPlayer.message(player, F.elem("Cancel ") + cur);
        }
    }

    private void handleDamage(LivingEntity damagee, LivingEntity damager, EntityDamageEvent.DamageCause cause, float damage, boolean ignoreArmor) {
        EntityLiving entityDamagee = ((CraftLivingEntity) damagee).getHandle();
        EntityLiving entityDamager = null;

        if (damager != null)
            entityDamager = ((CraftLivingEntity) damager).getHandle();

        entityDamagee.aC = 1.5F;

        if ((float) entityDamagee.noDamageTicks > (float) entityDamagee.maxNoDamageTicks / 2.0F) {
            if (damage <= entityDamagee.lastDamage) {
                return;
            }
            applyDamage(entityDamagee, damage - entityDamagee.lastDamage, ignoreArmor);
            entityDamagee.lastDamage = damage;
        } else {
            entityDamagee.lastDamage = damage;
            entityDamagee.previousHealth = entityDamagee.getHealth();
            applyDamage(entityDamagee, damage, ignoreArmor);
        }
        if (entityDamager != null)
            entityDamagee.b(entityDamager);

        if (entityDamager != null)
            if (entityDamager instanceof EntityHuman) {
                entityDamagee.lastDamageByPlayerTime = 100;
                entityDamagee.killer = (EntityHuman) entityDamager;
            }

        if (entityDamagee.getHealth() <= 0) {
            if (entityDamager != null) {
                if (entityDamager instanceof EntityHuman)
                    entityDamagee.die(DamageSource.playerAttack((EntityHuman) entityDamager));
                else
                    entityDamagee.die(DamageSource.mobAttack(entityDamager));
            } else
                entityDamagee.die(DamageSource.GENERIC);
        }
    }

    @EventHandler
    public void damageSound(CustomDamageEvent e) {
        if (e.isCancelled()) return;

        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
            return;

        //Damagee
        LivingEntity damagee = e.getDamageeEntity();
        if (damagee == null) return;

        // TODO ADD SUPPORT FOR DISGUISE WHEN CREATED

        //Sound
        Sound sound = Sound.HURT_FLESH;
        float vol = 1f;
        float pitch = 1f;

        //Armor Sound
        if (damagee instanceof Player) {
            Player player = (Player) damagee;

            double r = Math.random();

            ItemStack stack = null;
            if (r > 0.50) stack = player.getInventory().getChestplate();
            else if (r > 0.25) stack = player.getInventory().getLeggings();
            else if (r > 0.10) stack = player.getInventory().getHelmet();
            else stack = player.getInventory().getBoots();

            if (stack != null) {
                if (stack.getType().toString().contains("LEATHER_")) {
                    sound = Sound.SHOOT_ARROW;
                    pitch = 2f;
                } else if (stack.getType().toString().contains("CHAINMAIL_")) {
                    sound = Sound.ITEM_BREAK;
                    pitch = 1.4f;
                } else if (stack.getType().toString().contains("GOLD_")) {
                    sound = Sound.ITEM_BREAK;
                    pitch = 1.8f;
                } else if (stack.toString().contains("IRON_")) {
                    sound = Sound.BLAZE_HIT;
                    pitch = 0.7f;
                } else if (stack.getType().toString().contains("DIAMOND_")) {
                    sound = Sound.BLAZE_HIT;
                    pitch = 0.9f;
                }
            }
        }
        //Animal Sound
        else {
            UtilEnt.PlayDamageSound(damagee);
            return;
        }
        damagee.getWorld().playSound(damagee.getLocation(), sound, vol, pitch);
    }

    private void applyDamage(EntityLiving entityLiving, float damage, boolean ignoreArmor) {
        if (!ignoreArmor) {
            int j = 25 - entityLiving.br();
            float k = damage * (float) j;

            entityLiving.damageArmor(damage);
            damage = k / 25.0f;
        }
        entityLiving.setHealth(entityLiving.getHealth() - damage);
    }

    private void weaponDamage(EntityDamageEvent e, LivingEntity entity) {
        if (!(entity instanceof Player)) return;

        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Player damager = (Player) entity;

        if (useSimpleWeaponDamage) {
            if (e.getDamage() > 1)
                e.setDamage(e.getDamage() - 1);

            if (UtilGear.isWeapon(damager.getItemInHand()) && damager.getItemInHand().getType().name().contains("GOLD_"))
                e.setDamage(e.getDamage() + 2);

            return;
        }

        if (damager.getItemInHand() == null || !UtilGear.isWeapon(damager.getItemInHand())) {
            e.setDamage(1);
            return;
        }

        Material mat = damager.getItemInHand().getType();

        int damage = 6;

        if (mat.name().contains("WOOD")) damage -= 3;
        else if (mat.name().contains("STONE")) damage -= 2;
        else if (mat.name().contains("DIAMOND")) damage += 1;
        else if (mat.name().contains("GOLD")) damage += 0;

        e.setDamage(damage);
    }

    private LivingEntity getDamageeEntity(EntityDamageEvent e) {
        if (e.getEntity() instanceof LivingEntity) return (LivingEntity) e.getEntity();
        return null;
    }

    private Projectile getProjectile(EntityDamageEvent e) {
        if (!(e instanceof EntityDamageByEntityEvent)) return null;

        EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent) e;

        if (eventEE.getDamager() instanceof Projectile) return (Projectile) eventEE.getDamager();

        return null;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public void setConditionManager(ConditionManager conditionManager) {
        this.conditionManager = conditionManager;
    }
}