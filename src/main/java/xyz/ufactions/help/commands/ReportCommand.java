package xyz.ufactions.help.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.help.HelpModule;
import xyz.ufactions.help.data.Report;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

import java.util.List;

public class ReportCommand extends CommandBase<HelpModule> {

    public ReportCommand(HelpModule plugin) {
        super(plugin, "report");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            return getPlayerMatches(sender, args[0]);
        }
        return super.onTabComplete(sender, commandLabel, args);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (player.hasPermission("core.report.list")) {
                    UtilPlayer.message(player, F.main(Plugin.getName(), "Fetching reports..."));
                    Plugin.runAsync(() -> {
                        List<Report> reports = Plugin.getReportRepository().getReports();
                        if (reports.isEmpty()) {
                            UtilPlayer.message(player, F.error(Plugin.getName(), "There are no reports."));
                        } else {
                            for (Report report : reports) {
                                UtilPlayer.message(player, C.mHead + "id : " + C.mBody + report.getId());
                                UtilPlayer.message(player, C.mHead + "reporter : " + F.elem(report.getReporter().getName()));
                                UtilPlayer.message(player, C.mHead + "reported : " + F.elem(report.getReported().getName()));
                                UtilPlayer.message(player, C.mHead + "reason : " + F.elem(report.getReason()));
                            }
                        }
                    });
                    return;
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("close")) {
                if (player.hasPermission("core.report.close")) {
                    UtilPlayer.message(player, F.main(Plugin.getName(), "Closing report..."));
                    Plugin.runAsync(() -> {
                        Integer id = null;
                        OfflinePlayer target = null;
                        try {
                            Integer x = Integer.parseInt(args[1]);
                            id = x;
                        } catch (NumberFormatException e) {
                            target = Bukkit.getOfflinePlayer(args[1]);
                        }
                        if (id == null) {
                            if (target == null || !target.hasPlayedBefore()) {
                                UtilPlayer.message(player, F.error(Plugin.getName(), "Invalid ID or Player name input!"));
                            } else {
                                Plugin.getReportRepository().deleteReports(target);
                                UtilPlayer.message(player, F.main(Plugin.getName(), "Closed all reports from " + F.elem(target.getName()) + "."));
                            }
                        } else {
                            Plugin.getReportRepository().deleteReport(id);
                            UtilPlayer.message(player, F.main(Plugin.getName(), "Closed report linked to ID " + F.elem(String.valueOf(id)) + "."));
                        }
                    });
                    return;
                }
            }
        }
        if (args.length >= 2) {
            UtilPlayer.searchOffline(list -> {
                if (list.size() == 1) {
                    OfflinePlayer reported = list.get(0);
                    if (reported.getPlayer() == player) {
                        UtilPlayer.message(player, F.error(Plugin.getName(), "You cannot report yourself!"));
                        return;
                    }
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i] + " ");
                    }
                    Report report = new Report(player, reported, builder.toString().trim());
                    UtilPlayer.message(player, F.main(Plugin.getName(), "Submitting report..."));
                    Plugin.runAsync(() -> {
                        int id = Plugin.getReportRepository().createReport(report);
                        UtilPlayer.message(player, F.main(Plugin.getName(), "Report Created!"));
                    });
                }
            }, player, args[0], true);
            return;
        }
        UtilPlayer.message(player, F.help("/report <player> <reason>", "Report a player!"));
        if (player.hasPermission("core.report.close")) {
            UtilPlayer.message(player, F.help("/report close <id|reported>", "If an ID is input it will close the report linked to that ID; If a player name is input it will close all reports linked to that player."));
        }
        if (player.hasPermission("core.report.list")) {
            UtilPlayer.message(player, F.help("/report list", "View all reports!"));
        }
    }
}