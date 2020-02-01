package xyz.ufactions.help.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.help.HelpModule;
import xyz.ufactions.help.data.TodoStatus;
import xyz.ufactions.help.data.TodoTask;
import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;

import java.util.List;

public class TodoCommand extends CommandBase<HelpModule> {

	public TodoCommand(HelpModule module) {
		super(module, "todo");
	}

	@Override
	public void execute(final Player caller, String[] args) {
		if (!caller.hasPermission("core.command.todo")) {
			UtilPlayer.message(caller, F.noPermission());
		} else {
			if (args.length == 0) {
				UtilPlayer.message(caller, F.main(Plugin.getName(), "Retreiving tasks."));

				Plugin.runAsync(new Runnable() {

					@Override
					public void run() {
						List<TodoTask> list = Plugin.getTodoRepository().loadTasks();
						if (list.isEmpty()) {
							UtilPlayer.message(caller, F.main(Plugin.getName(), "There are no tasks todo!"));
						} else {
							UtilPlayer.message(caller, C.mHead + C.Strike + "----------------------------");
							for (TodoTask task : list) {
								String priority = "";
								if (task.getPriority() == 4) {
									priority = C.cDRed + task.getPriority();
								} else if (task.getPriority() == 3 || task.getPriority() == 2) {
									priority = C.cYellow + task.getPriority();
								} else {
									priority = C.cGreen + task.getPriority();
								}
								priority = priority + ChatColor.RESET;
								UtilPlayer.message(caller, F.elem(String.valueOf(task.getId())) + " " + priority + " "
										+ task.getTask() + " " + task.getStatus().getName());
							}
							UtilPlayer.message(caller, C.mHead + C.Strike + "----------------------------");
						}
					}
				});
				return;
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("status")) {
					int id = -1;
					try {
						id = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						UtilPlayer.message(caller, F.error(Plugin.getName(), "Invalid numerical value."));
						return;
					}
					TodoStatus status = null;
					try {
						status = TodoStatus.valueOf(args[2].toUpperCase());
					} catch (Exception e) {
						String a = C.cYellow + "";
						for (TodoStatus b : TodoStatus.values()) {
							a += ", " + b.toString();
						}
						UtilPlayer.message(caller, F.error(Plugin.getName(), "Unknown Status Type. " + a));
						return;
					}
					Plugin.updateStatus(caller, id, status);
					return;
				}
			}
			if (args.length >= 3) {
				if (args[0].equalsIgnoreCase("add")) {
					int priority = 1;
					try {
						priority = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						UtilPlayer.message(caller, F.error(Plugin.getName(), "Invalid numerical value."));
						return;
					}
					if(priority < 0) {
						UtilPlayer.message(caller, F.error(Plugin.getName(), ""));
						return;
					}
					if(priority > 4) {
						UtilPlayer.message(caller, F.error(Plugin.getName(), "Maximum priorty value '4'. 1 Low, 2 Medium, 3 High, 4 Urgent priority"));
						return;
					}
					String task = "";
					for (int i = 2; i < args.length; i++) {
						task += args[i] + " ";
					}
					task = task.trim();
					Plugin.addTask(caller, priority, task);
					return;
				}
			}
			UtilPlayer.message(caller, F.help("/todo", "View all of the todo tasks."));
			UtilPlayer.message(caller, F.help("/todo add <priorty> <task...>", "Add a todo task."));
			UtilPlayer.message(caller, F.help("/todo status <id> <status>", "Update the task's status."));
			UtilPlayer.message(caller, F.help("/todo priority <id> <priority>", "Update the task's priority."));
			UtilPlayer.message(caller, C.cGray + C.Italics + "Maximum priorty value '4'. 1 Low, 2 Medium, 3 High, 4 Urgent priority");
		}
	}
}