package xyz.ufactions.combat;

import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.api.Module;
import xyz.ufactions.damage.CustomDamageEvent;
import xyz.ufactions.libs.*;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

import java.util.HashSet;
import java.util.Iterator;

public class CombatManager extends Module {

    public enum AttackReason {
        Attack,
        CustomWeaponName,
        DefaultWeaponName
    }

    private NautHashMap<Player, CombatLog> active = new NautHashMap<>();
    private NautHashMap<String, ClientCombat> combatClients = new NautHashMap<>();

    private HashSet<Player> removeList = new HashSet<>();

    protected long expireTime = 15000;

    protected AttackReason attackReason = AttackReason.CustomWeaponName;

    public CombatManager(JavaPlugin plugin) {
        super("Combat", plugin);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        combatClients.remove(e.getPlayer().getName());
    }

    public ClientCombat get(String name) {
        if (!combatClients.containsKey(name)) combatClients.put(name, new ClientCombat());

        return combatClients.get(name);
    }

    //This is a backup, for when CustomDamageEvent is disabled (manually)
    @EventHandler(priority = EventPriority.MONITOR)
    public void addAttack(EntityDamageEvent e) {
        if (e.isCancelled()) return;

        if (e.getEntity() == null || !(e.getEntity() instanceof Player)) return;

        Player damagee = (Player) e.getEntity();

        LivingEntity damagerEnt = UtilEvent.GetDamagerEntity(e, true);

        //Attacked by Entity
        if (damagerEnt != null) {
            if (damagerEnt instanceof Player)
                get((Player) damagerEnt).setLastCombat(System.currentTimeMillis());

            get(damagee).attacked(UtilEnt.getName(damagerEnt), e.getDamage(), damagerEnt, e.getCause() + "", null);
        }
        //Damager is WORLD
        else {
            EntityDamageEvent.DamageCause cause = e.getCause();

            String source = "n/a";
            String reason = "n/a";

            if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                source = "Explosion";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
                source = "Cactus";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.CUSTOM) {
                source = "Custom";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
                source = "Water";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                source = "Entity";
                reason = "Attack";
            } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                source = "Explosion";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FALL) {
                source = "Fall";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                source = "Falling Block";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FIRE) {
                source = "Fire";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                source = "Fire";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.LAVA) {
                source = "Lava";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
                source = "Lightning";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
                source = "Magic";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.MELTING) {
                source = "Melting";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.POISON) {
                source = "Poison";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                source = "Projectile";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
                source = "Starvation";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                source = "Suffocation";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
                source = "Suicide";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.VOID) {
                source = "Void";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.WITHER) {
                source = "Wither";
                reason = "n/a";
            }

            get(damagee).attacked(source, e.getDamage(), null, reason, null);
        }
    }

    public void addAttack(CustomDamageEvent e) {
        // Not Player > No Log
        if (e.getDamageePlayer() == null) return;

        // Damager is ENTITY
        if (e.getDamagerEntity(true) != null) {
            String reason = e.getReason();
            if (reason == null) {
                if (e.getDamagerPlayer(false) != null) {
                    Player damager = e.getDamagerPlayer(false);
                    reason = "Attack";
                    if (attackReason == AttackReason.DefaultWeaponName) {
                        reason = "{Penis Music}";
                        if (damager.getItemInHand() != null) {
                            byte data = 0;
                            if (damager.getItemInHand().getData() != null)
                                data = damager.getItemInHand().getData().getData();

                            reason = "{Unknown Default Weapon Name}";
                        }
                    } else if (attackReason == AttackReason.CustomWeaponName) {
                        reason = "{Penis Music}";
                        if (damager.getItemInHand() != null) {
                            ItemStack itemStack = CraftItemStack.asNMSCopy(damager.getItemInHand());

                            if (itemStack != null) {
                                reason = itemStack.getName();
                            }
                        }
                    }
                } else if (e.getProjectile() != null) {
                    if (e.getProjectile() instanceof Arrow)
                        reason = "Archery";
                    else if (e.getProjectile() instanceof Fireball)
                        reason = "Fireball";
                }
            }

            if (e.getDamagerEntity(true) instanceof Player)
                get((Player) e.getDamagerEntity(true)).setLastCombat(System.currentTimeMillis());

            get(e.getDamageePlayer()).attacked(UtilEnt.getName(e.getDamagerEntity(true)), (int) e.getDamage(), e.getDamagerEntity(true), reason, e.getDamageMod());
        }
        // Damager is WORLD
        else {
            EntityDamageEvent.DamageCause cause = e.getCause();

            String source = "?";
            String reason = "n/a";

            if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                source = "Explosion";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
                source = "Cactus";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.CUSTOM) {
                source = "Custom";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
                source = "Water";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                source = "Entity";
                reason = "Attack";
            } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                source = "Explosion";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FALL) {
                source = "Fall";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                source = "Falling Block";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FIRE) {
                source = "Fire";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                source = "Fire";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.LAVA) {
                source = "Lava";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
                source = "Lightning";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
                source = "Magic";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.MELTING) {
                source = "Melting";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.POISON) {
                source = "Poison";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                source = "Projectile";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
                source = "Starvation";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                source = "Suffocation";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
                source = "Suicide";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.VOID) {
                source = "Void";
                reason = "n/a";
            } else if (cause == EntityDamageEvent.DamageCause.WITHER) {
                source = "Wither";
                reason = "n/a";
            }
            if (e.getReason() != null) reason = e.getReason();

            get(e.getDamageePlayer()).attacked(source, (int) e.getDamage(), null, reason, e.getDamageMod());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);

        if (!active.containsKey(e.getEntity())) return;

        CombatLog log = active.remove(e.getEntity());
        log.setDeathTime(System.currentTimeMillis());

        //Save Death
        get(e.getEntity().getName()).getDeaths().addFirst(log);

        //Add Kill/Assist
        int assists = 0;
        for (int i = 0; i < log.getAttackers().size(); i++) {
            if (!log.getAttackers().get(i).isPlayer()) continue;

            if (UtilTime.elapsed(log.getAttackers().get(i).getLastDamage(), expireTime)) continue;

            if (log.getKiller() == null) {
                log.setKiller(log.getAttackers().get(i));

                ClientCombat killerClient = get(log.getAttackers().get(i).getName());

                if (killerClient != null) killerClient.getKills().addFirst(log);
            } else {
                assists++;

                ClientCombat assistClient = get(log.getAttackers().get(i).getName());

                if (assistClient != null) assistClient.getAssists().addFirst(log);
            }
        }
        log.setAssists(assists);

        //Event
        CombatDeathEvent deathEvent = new CombatDeathEvent(e, get(e.getEntity().getName()), log);
        UtilServer.getServer().getPluginManager().callEvent(deathEvent);

        //XXX Death MSG
        if (deathEvent.getBroadcastType() == DeathMessageType.Detailed || deathEvent.getBroadcastType() == DeathMessageType.Absolute) {
            //Display Simple
            for (Player cur : e.getEntity().getWorld().getPlayers()) {
                //Killed
                String killedColor = log.getKilledColor();

                String deadPlayer = killedColor + e.getEntity().getName();

                //Killer
                if (log.getKiller() != null) {
                    String killerColor = log.getKillerColor();

                    String killPlayer = killerColor + log.getKiller().getName();

                    if (log.getAssists() > 0)
                        killPlayer += "+ " + log.getAssists();

                    String weapon = log.getKiller().getLastDamageSource();

                    UtilPlayer.message(cur, F.main("Death", deadPlayer + C.mBody + " was killed by " + killPlayer + C.mBody + " with " + F.item(weapon) + "."));
                }
                //No Killer
                else {
                    if (log.getAttackers().isEmpty())
                        UtilPlayer.message(cur, F.main("Death", deadPlayer + C.mBody + " has died."));
                    else {
                        if (log.getLastDamager() != null && log.getLastDamager().getReason() != null && log.getLastDamager().getReason().length() > 1) {
                            UtilPlayer.message(cur, F.main("Death", deadPlayer + C.mBody + " was killed by " + F.name(log.getLastDamager().getReason()) + "."));
                        } else {
                            UtilPlayer.message(cur, F.main("Death", deadPlayer + C.mBody + " was killed by " + F.name(log.getAttackers().getFirst().getName()) + "."));
                        }
                    }
                }
            }

            // Self Detail
            if (deathEvent.getBroadcastType() == DeathMessageType.Absolute)
                UtilPlayer.message(e.getEntity(), log.displayAbsolute());
            else
                UtilPlayer.message(e.getEntity(), log.display());
        } else if (deathEvent.getBroadcastType() == DeathMessageType.Simple) {
            if (log.getKiller() != null) {
                //Killer
                String killerColor = log.getKillerColor();
                String killPlayer = killerColor + log.getKiller().getName();

                //Killed
                String killedColor = log.getKilledColor();
                String deadPlayer = killedColor + e.getEntity().getName();

                if (log.getAssists() > 0)
                    killPlayer += "+ " + log.getAssists();

                String weapon = log.getKiller().getLastDamageSource();

                Player killer = UtilPlayer.searchExact(log.getKiller().getName());
                UtilPlayer.message(killer, F.main("Death", "You killed " + F.elem(deadPlayer) + " with " + F.item(weapon) + "."));

                UtilPlayer.message(e.getEntity(), F.main("Death", killPlayer + C.mBody + " killed you with " + F.item(weapon) + "."));
            } else {
                if (log.getAttackers().isEmpty()) {
                    UtilPlayer.message(e.getEntity(), F.main("Death", "You have died."));
                } else {
                    UtilPlayer.message(e.getEntity(), F.main("Death", "You were killed by " + F.name(log.getAttackers().getFirst().getName()) + C.mBody + "."));
                }
            }
        }
    }

    @EventHandler
    public void expireOld(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        for (CombatLog log : active.values())
            log.expireOld();
    }

    public void add(Player player) {
        active.put(player, new CombatLog(player, 15000));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clear(ClearCombatEvent e) {
        active.remove(e.getPlayer());
    }

    public CombatLog get(Player player) {
        if (!active.containsKey(player)) {
            add(player);
        }

        return active.get(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clearInactives(UpdateEvent e) {
        if (e.getType() == UpdateType.MIN_02) {
            //Remove already marked inactives if still offline
            Iterator<Player> removeIterator = removeList.iterator();
            while (removeIterator.hasNext()) {
                Player player = removeIterator.next();

                if (!player.isOnline())
                    active.remove(player);

                removeIterator.remove();
            }

            // Mark inactive for cleanup next go around
            for (Player player : active.keySet()) {
                if (!player.isOnline())
                    removeList.add(player);
            }
        }
    }

    public void debugInfo(Player player)
    {
        StringBuilder nameBuilder = new StringBuilder();

        for (Player combats : active.keySet())
        {
            if (!combats.isOnline())
            {
                if (nameBuilder.length() != 0)
                    nameBuilder.append(", ");

                nameBuilder.append(combats.getName());
            }
        }

        player.sendMessage(F.main(getName(), nameBuilder.toString()));
    }

    public void setUseWeaponName(AttackReason attackReason) {
        this.attackReason = attackReason;
    }

    public AttackReason getUseWeaponName() {
        return attackReason;
    }
}