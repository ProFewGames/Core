package xyz.ufactions.motd.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilMath;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.motd.MOTDModule;

public class MOTDCommand extends CommandBase<MOTDModule> {

    public MOTDCommand(MOTDModule plugin) {
        super(plugin, "motd");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            UtilPlayer.message(player, F.main(Plugin.getName(), "list:"));
            Plugin.getMOTDs().forEach((integer, s) -> UtilPlayer.message(player, C.cYellow + integer + " " + s));
            return;
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                if (!UtilMath.isInteger(args[1])) {
                    UtilPlayer.message(player, F.error(Plugin.getName(), "Invalid Integer"));
                    return;
                }
                int id = Integer.parseInt(args[1]);
                Plugin.deleteMOTD(id);
                UtilPlayer.message(player, F.main(Plugin.getName(), "Deleted MOTD linked to ID " + F.elem(String.valueOf(id)) + "."));
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                String motd = F.concatenate(1, " ", args);
                Plugin.addMOTD(motd);
                UtilPlayer.message(player, F.main(Plugin.getName(), "Added '" + motd + C.mBody + "' to the MOTD registrar."));
                return;
            }
        }
        UtilPlayer.message(player, F.help("/motd", "View all motds"));
        UtilPlayer.message(player, F.help("/motd add <motd>", "Add <motd> to the server list"));
        UtilPlayer.message(player, F.help("/motd delete <id>", "Delete the motd linked to <id>"));
    }
}
