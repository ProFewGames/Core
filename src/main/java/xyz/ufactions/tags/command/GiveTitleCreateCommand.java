package xyz.ufactions.tags.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.tags.TitleModule;

import java.util.ArrayList;
import java.util.List;

public class GiveTitleCreateCommand extends CommandBase<TitleModule> {

    public GiveTitleCreateCommand(TitleModule module) {
        super(module, "givetitlecreate");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission("core.command.givetitlecreate")) {
            if (args.length == 1) {
                return getMatches(args[0], UtilServer.getPlayerNames());
            }
        }
        return super.onTabComplete(sender, commandLabel, args);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(final Player player, final String[] args) {
        if (!player.hasPermission("core.command.givetitlecreate")) {
            player.sendMessage(F.noPermission());
            return;
        }
        if (args.length > 1) {
            List<String> names = new ArrayList<>();
            for (OfflinePlayer pls : Bukkit.getOfflinePlayers()) {
                String name = pls.getName();
                if (name.startsWith(args[0])) {
                    if (!names.contains(name)) {
                        names.add(name);
                    }
                }
            }
            UtilPlayer.searchOffline(list -> {
                if (list.size() == 1) {
                    OfflinePlayer target = list.get(0);
                    int amount = 0;
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        UtilPlayer.message(player, F.help("/givetitlecreate <player> <n>",
                                "Give <player> <n> amount of /titlecreate tokens."));
                        return;
                    }
                    Plugin.waitForResponse(player, target.getUniqueId(),
                            F.elem("%name%") + " now has " + F.elem("%amount%") + " titlecreate credits.");
                    Plugin.getTagManager().updateTokens(target.getUniqueId(), amount);
                    String dname = (!target.isOnline() ? target.getName() : target.getPlayer().getDisplayName());
                    UtilPlayer.message(player,
                            F.main(Plugin.getName(), "You have gave " + F.elem(dname + " " + amount) + " tokens."));
                    if (target.isOnline()) {
                        UtilPlayer.message(target.getPlayer(), F.main(Plugin.getName(),
                                F.elem(player.getName()) + " has gave you " + F.elem(amount + "") + " tokens."));
                    }
                }
            }, player, args[0], true);
        } else {
            UtilPlayer.message(player,
                    F.help("/givetitlecreate <player> <n>", "Give <player> <n> amount of /titlecreate tokens."));
        }
    }
}