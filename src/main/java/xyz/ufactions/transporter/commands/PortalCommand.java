package xyz.ufactions.transporter.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.selections.data.Selection;
import xyz.ufactions.transporter.TransporterModule;

public class PortalCommand extends CommandBase<TransporterModule> {

    public PortalCommand(TransporterModule module) {
        super(module, "portal");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 1) {
            if (!Plugin.getSelectionManager().validateSelection(player.getUniqueId())) {
                UtilPlayer.message(player, F.error(Plugin.getSelectionManager().getName(), "You do not have anything selected!"));
                return;
            }
            Selection selection = Plugin.getSelectionManager().getSelection(player.getUniqueId());
            String destination = args[0];
            Plugin.setDestination(selection, destination);
            UtilPlayer.message(player, F.main(Plugin.getName(), "This portal now leads to " + F.elem(destination) + "."));
            return;
        }
        UtilPlayer.message(player, F.help("/portal <destination>", "Set the destination for the currently selected portal"));
    }
}