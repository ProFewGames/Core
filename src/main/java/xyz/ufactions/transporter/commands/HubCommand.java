package xyz.ufactions.transporter.commands;

import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.transporter.TransporterModule;

public class HubCommand extends CommandBase<TransporterModule> {

    public HubCommand(TransporterModule module) {
        super(module, "hub", "lobby");
    }

    @Override
    public void execute(Player player, String[] strings) {
        Plugin.transfer(player.getName(), "Hub");
    }
}