package xyz.ufactions.creature.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.creature.Creature;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class HelpCommand extends CommandBase<Creature> {

    public HelpCommand(Creature plugin) {
        super(plugin, "help");
    }

    @Override
    public void execute(Player caller, String[] args) {
        UtilPlayer.message(caller, F.main(Plugin.getName(), "Commands List;"));
        UtilPlayer.message(caller, F.help("/mob", "List Entities"));
        UtilPlayer.message(caller, F.help("/mob kill <Type>", "Remove Entities of Type"));
        UtilPlayer.message(caller, F.help("/mob <Type> (# baby lock angry s# <Prof>)", "Create"));
        UtilPlayer.message(caller, F.help("Professions", "Butcher, Blacksmith, Farmer, Librarian, Priest"));
    }
}
