package xyz.ufactions.playtime.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.playtime.PlaytimeModule;

import java.util.List;

public class PlaytimeCommand extends CommandBase<PlaytimeModule> {

    public PlaytimeCommand(PlaytimeModule module) {
        super(module, "playtime");
    }

    @Override
    public void execute(final Player caller, String[] args) {
        if (args.length == 1) {
            String lookingFor = args[0];
            UtilPlayer.searchOffline(list -> {
                if (list.size() == 1) {
                    a(caller, list.get(0));
                }
            }, caller, args[0], true);
            return;
        }
        a(caller, caller);
    }

    private void a(Player a, OfflinePlayer b) {
        String c;
        if (a.getUniqueId() != b.getUniqueId()) {
            c = F.elem(b.getName()) + "'s";
        } else {
            c = "Your";
        }
        UtilPlayer.message(a, F.main(Plugin.getName(), c + " total playtime is: "
                + F.elem(Plugin.getDurationBreakdown(Plugin.getUser(b).getPlayTime()) + ".")));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        return getPlayerMatches(sender, args[0]);
    }
}