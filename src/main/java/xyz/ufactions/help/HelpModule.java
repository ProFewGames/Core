package xyz.ufactions.help;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.help.commands.Changelog;
import xyz.ufactions.help.commands.ReportCommand;
import xyz.ufactions.help.commands.TodoCommand;
import xyz.ufactions.help.data.ChangeLog;
import xyz.ufactions.help.data.TodoStatus;
import xyz.ufactions.help.data.TodoTask;
import xyz.ufactions.help.repository.ChangelogRepository;
import xyz.ufactions.help.repository.ReportRepository;
import xyz.ufactions.help.repository.TODORepository;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HelpModule extends Module {

    private ReportRepository reportRepository;
    private TODORepository repository;
    private ChangelogRepository changelog;

    private List<String> developers;

    private HashMap<UUID, Integer> notified = new HashMap<>();

    public HelpModule(JavaPlugin plugin) {
        super("Help", plugin);

        reportRepository = new ReportRepository(plugin);
        changelog = new ChangelogRepository(plugin);
        repository = new TODORepository(plugin);

        developers = new ArrayList<>();
        developers.add("ProFewGames");
        developers.add("JynxDEV");
    }

    @Override
    public void addCommands() {
        addCommand(new Changelog(this));
        addCommand(new TodoCommand(this));
        addCommand(new ReportCommand(this));
    }

//    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        notified.put(player.getUniqueId(), 0);

        if (player.hasPermission("core.command.changelog")) {
            runAsync(new Runnable() {

                @Override
                public void run() {
                    List<ChangeLog> changes = getChangelog().getChanges(5);
                    if (changes.isEmpty()) {
                        UtilPlayer.message(player, F.main(Plugin.getName(), "There have been no changes made while you were offline!"));
                    } else {
                    	int id = notified.get(player.getUniqueId());
                    	boolean messaged = false;
                    	int top = id;
                        for (ChangeLog change : changes) {
                        	if(id < change.getId()) {
                        		if(!messaged) {
									UtilPlayer.message(player, C.mHead + C.Strike + "----------------------------");
									messaged = true;
								}
								UtilPlayer.message(player, C.mHead + "- " + C.mBody + change.getChange());
                        		top = change.getId();
							}
                        }
                        notified.remove(player.getUniqueId());
						notified.put(player.getUniqueId(), top);
						if(messaged) {
							UtilPlayer.message(player, C.mHead + C.Strike + "----------------------------");
						}
                    }
                }
            });
        }
    }

    public ChangelogRepository getChangelog() {
        return changelog;
    }

    public TODORepository getTodoRepository() {
        return repository;
    }

    public void addTask(final Player player, final int priority, final String task) {
        UtilPlayer.message(player, F.main(Plugin.getName(), "Adding todo task..."));
        runAsync(new Runnable() {

            @Override
            public void run() {
                repository.addTask(new TodoTask(-1, task, player.getName(), priority, TodoStatus.WAITING));
                UtilPlayer.message(player, F.main(Plugin.getName(), "TODO task added!"));
            }
        });
    }

    public void updatePriority(final Player player, final int id, final int priority) {
        runAsync(new Runnable() {

            @Override
            public void run() {
                final TodoTask task = findTask(player, id);
                if (task == null)
                    return;
                runSync(new Runnable() {

                    @Override
                    public void run() {
                        update(player, task, priority, task.getStatus());
                    }
                });
            }
        });
    }

    public void updateStatus(final Player player, final int id, final TodoStatus status) {
        runAsync(new Runnable() {

            @Override
            public void run() {
                final TodoTask task = findTask(player, id);
                if (task == null)
                    return;
                runSync(new Runnable() {

                    @Override
                    public void run() {
                        update(player, task, task.getPriority(), status);
                    }
                });
            }
        });
    }

    private TodoTask findTask(Player player, int id) {
        TodoTask task = repository.getTask(id);
        if (task == null) {
            UtilPlayer.message(player,
                    F.error(Plugin.getName(), "Cannot find todo task with id " + F.elem(String.valueOf(id) + ".")));
        }
        return task;
    }

    private void update(final Player player, final TodoTask task, final int priority, final TodoStatus status) {
        UtilPlayer.message(player, F.main(Plugin.getName(), "Updating todo task..."));
        runAsync(new Runnable() {

            @Override
            public void run() {
                repository.updateTask(task, priority, status);
                UtilPlayer.message(player, F.main(Plugin.getName(), "Updated task."));
            }
        });
    }

    public boolean isDeveloper(Player player) {
        return developers.contains(player.getName());
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }
}