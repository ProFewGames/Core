package xyz.ufactions.creature.commands;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.creature.Creature;
import xyz.ufactions.creature.events.CreatureKillEntitiesEvent;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilEnt;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;

import java.util.ArrayList;
import java.util.List;

public class KillCommand extends CommandBase<Creature> {

    public KillCommand(Creature module) {
        super(module, "kill", "k");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            UtilPlayer.message(player, F.error(Plugin.getName(), "Missing Entity Type Parameter."));
            return;
        }

        EntityType type = UtilEnt.searchEntity(player, args[0], true);

        if (type == null && !args[0].equalsIgnoreCase("all")) return;

        int count = 0;
        List<Entity> killList = new ArrayList<>();

        for (World world : UtilServer.getServer().getWorlds()) {
            for (Entity ent : world.getEntities()) {
                if (ent.getType() == EntityType.PLAYER) continue;
                if (type == null || ent.getType() == type) {
                    killList.add(ent);
                }
            }
        }
        CreatureKillEntitiesEvent event = new CreatureKillEntitiesEvent(killList);
        Plugin.getPlugin().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        for (Entity entity : event.getEntities()) {
            entity.remove();
            ;
            count++;
        }
        String target = "ALL";
        if (type != null)
            target = UtilEnt.getName(type);

        UtilPlayer.message(player, F.main(Plugin.getName(), "Killed " + F.elem(target) + ". " + F.elem(String.valueOf(count)) + " removed."));
    }
}