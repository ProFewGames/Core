package xyz.ufactions.help.commands;

import java.util.List;

import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.help.HelpModule;
import xyz.ufactions.help.data.ChangeLog;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

public class Changelog extends CommandBase<HelpModule> {

    public Changelog(HelpModule module) {
        super(module, "changelog");
    }

    @Override
    public void execute(final Player caller, final String[] args) {
        if (!caller.hasPermission("core.command.changelog")) {
            UtilPlayer.message(caller, F.noPermission());
        } else {
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (Plugin.isDeveloper(caller)) {
                        UtilPlayer.message(caller, F.main(Plugin.getName(), "Adding change to changelog..."));
                        Plugin.runAsync(new Runnable() {

                            @Override
                            public void run() {
                                String change = "";
                                for (int i = 1; i < args.length; i++) {
                                    change += args[i] + " ";
                                }
                                change = change.trim();
                                Plugin.getChangelog().createChange(change);
                                UtilPlayer.message(caller, F.main(Plugin.getName(), "Added change to changes!"));

                            }
                        });
                        return;
                    }
                }
            }
            UtilPlayer.message(caller, F.main(Plugin.getName(), "Retrieving changes"));
            Plugin.runAsync(new Runnable() {

                @Override
                public void run() {
                    List<ChangeLog> changes = Plugin.getChangelog().getChanges();
                    if (changes.isEmpty()) {
                        UtilPlayer.message(caller, F.main(Plugin.getName(), "There are no changes."));
                    } else {
                        UtilPlayer.message(caller, C.mHead + C.Strike + "----------------------------");
                        for (ChangeLog change : changes) {
                            UtilPlayer.message(caller, C.mHead + "- " + C.mBody + change.getChange());
                        }
                        UtilPlayer.message(caller, C.mHead + C.Strike + "----------------------------");
                    }
                }
            });
        }
    }
}