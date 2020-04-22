package xyz.ufactions.creature.commands;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import xyz.ufactions.commands.MultiCommandBase;
import xyz.ufactions.creature.Creature;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilEnt;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;

import java.util.HashMap;
import java.util.HashSet;

public class MobCommand extends MultiCommandBase<Creature> {

    public MobCommand(Creature plugin) {
        super(plugin, "mob");

        addCommand(new KillCommand(plugin));
        addCommand(new HelpCommand(plugin));
    }

    @Override
    protected void help(Player player, String[] args) {
        if (args == null) {
            HashMap<EntityType, Integer> entMap = new HashMap<>();

            int count = 0;
            for (World world : UtilServer.getServer().getWorlds()) {
                for (Entity ent : world.getEntities()) {
                    if (!entMap.containsKey(ent.getType())) entMap.put(ent.getType(), 0);
                    entMap.put(ent.getType(), 1 + entMap.get(ent.getType()));
                    count++;
                }
            }
            UtilPlayer.message(player, F.main(Plugin.getName(), "Listing Entities:"));
            for (EntityType cur : entMap.keySet()) {
                UtilPlayer.message(player, F.help(UtilEnt.getName(cur), entMap.get(cur) + ""));
            }
            UtilPlayer.message(player, F.help("Total", count + ""));
        } else {
            EntityType type = UtilEnt.searchEntity(player, args[0], true);

            if (type == null) return;

            UtilPlayer.message(player, F.main(Plugin.getName(), "Spawning Creature(s);"));

            //Store Args
            HashSet<String> argSet = new HashSet<>();
            for (int i = 1; i < args.length; i++)
                if (args[i].length() > 0)
                    argSet.add(args[i]);

            // Search Count
            int count = 1;
            HashSet<String> argHandle = new HashSet<>();
            for (String arg : argSet) {
                try {
                    int newCount = Integer.parseInt(arg);

                    if (newCount <= 0) continue;

                    //Set Count
                    count = newCount;
                    UtilPlayer.message(player, F.help("Amount", count + ""));

                    //Flag Arg
                    argHandle.add(arg);
                    break;
                } catch (Exception e) {
                    //None
                }
            }
            for (String arg : argHandle)
                argSet.remove(arg);

            //Spawn
            HashSet<Entity> entSet = new HashSet<>();
            for (int i = 0; i < count; i++) {
                entSet.add(Plugin.spawnEntity(player.getTargetBlock((HashSet<Byte>) null, 0).getLocation().add(0.5, 1, 0.5), type));
            }
            //Search vars
            for (String arg : argSet) {
                if (arg.length() == 0) continue;

                else if (arg.equalsIgnoreCase("baby") || arg.equalsIgnoreCase("b")) {
                    for (Entity ent : entSet) {
                        if (ent instanceof Ageable)
                            ((Ageable) ent).setBaby();
                        else if (ent instanceof Zombie)
                            ((Zombie) ent).setBaby(true);
                    }
                    UtilPlayer.message(player, F.help("Baby", "True"));
                    argHandle.add(arg);
                }
                // Lock
                else if (arg.equalsIgnoreCase("age") || arg.equalsIgnoreCase("lock")) {
                    for (Entity ent : entSet)
                        if (ent instanceof Ageable) {
                            ((Ageable) ent).setAgeLock(true);
                            UtilPlayer.message(player, F.help("Age", "False"));
                        }
                }
                // Angry
                else if (arg.equalsIgnoreCase("angry") || arg.equalsIgnoreCase("a")) {
                    for (Entity ent : entSet)
                        if (ent instanceof Wolf)
                            ((Wolf) ent).setAngry(true);

                    for (Entity ent : entSet)
                        if (ent instanceof Skeleton)
                            ((Skeleton) ent).setSkeletonType(Skeleton.SkeletonType.WITHER);

                    UtilPlayer.message(player, F.help("Angry", "True"));
                }
                // Profession
                else if (arg.toLowerCase().charAt(0) == 'p') {
                    try {
                        String prof = arg.substring(1, arg.length());

                        Villager.Profession profession = null;
                        for (Villager.Profession cur : Villager.Profession.values())
                            if (cur.name().toLowerCase().contains(prof.toLowerCase()))
                                profession = cur;

                        UtilPlayer.message(player, F.help("Profession", profession.name()));

                        for (Entity ent : entSet)
                            if (ent instanceof Villager)
                                ((Villager) ent).setProfession(profession);
                    } catch (Exception e) {
                        UtilPlayer.message(player, F.help("Profession", "Invalid [" + arg + "] on " + type.name()));
                    }
                    argHandle.add(arg);
                } else if (arg.toLowerCase().charAt(0) == 'n' && arg.length() > 1) {
                    try {
                        String name = "";
                        for (char c : arg.substring(1, arg.length()).toCharArray()) {
                            if (c != '_')
                                name += c;
                            else
                                name += " ";
                        }

                        for (Entity ent : entSet) {
                            if (ent instanceof CraftLivingEntity) {
                                CraftLivingEntity cEnt = (CraftLivingEntity) ent;
                                cEnt.setCustomName(name);
                                cEnt.setCustomNameVisible(true);
                            }
                        }
                    } catch (Exception e) {
                        UtilPlayer.message(player, F.help("Size", "Invalid [" + arg + "] on " + type.getName()));
                    }
                    argHandle.add(arg);
                }
            }
            for (String arg : argHandle)
                argSet.remove(arg);

            for (String arg : argSet)
                UtilPlayer.message(player, F.help("Unhandled", arg));

            // Inform
            UtilPlayer.message(player, F.main(Plugin.getName(), "Spawned " + count + " " + UtilEnt.getName(type) + "."));
        }
    }
}