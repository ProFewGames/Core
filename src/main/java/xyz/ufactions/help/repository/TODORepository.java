package xyz.ufactions.help.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.SourceType;
import xyz.ufactions.database.column.ColumnInt;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.help.data.TodoStatus;
import xyz.ufactions.help.data.TodoTask;

public class TODORepository extends RepositoryBase {

	private final String CREATE_TODO_TABLE = "CREATE TABLE IF NOT EXISTS `todo` (`id` INT AUTO_INCREMENT NOT NULL, `submitter` VARCHAR(16) NOT NULL, `task` VARCHAR(100), `priority` INT, `status` VARCHAR(16), PRIMARY KEY(id));";
	private final String RETREIVE_TODO = "SELECT * FROM `todo` WHERE 1;";
	private final String INSERT_TODO = "INSERT INTO `todo` (`submitter`, `task`, `priority`, `status`) VALUES (?, ?, ?, ?);";
	private final String UPDATE_TODO = "UPDATE `todo` SET `priority`=?,`status`=? WHERE `id`=?;";

	public TODORepository(JavaPlugin plugin) {
		super(plugin, DBPool.getSource(SourceType.NETWORK));
	}

	public final void addTask(TodoTask task) {
		executeUpdate(INSERT_TODO, new ColumnVarChar("submitter", 16, task.getSubmitter()),
				new ColumnVarChar("task", 100, task.getTask()), new ColumnInt("priority", task.getPriority()),
				new ColumnVarChar("status", 16, task.getStatus().toString()));
	}

	public final void updateTask(TodoTask task, int priority, TodoStatus status) {
		executeUpdate(UPDATE_TODO, new ColumnInt("priority", priority),
				new ColumnVarChar("status", 16, status.toString()), new ColumnInt("id", task.getId()));
	}

	public final TodoTask getTask(int id) {
		for (TodoTask task : loadTasks()) {
			if (task.getId() == id) {
				return task;
			}
		}
		return null;
	}

	public final List<TodoTask> loadTasks() {
		final List<TodoTask> list = new ArrayList<>();
		executeQuery(RETREIVE_TODO, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					list.add(new TodoTask(resultSet.getInt(1), resultSet.getString(3), resultSet.getString(2),
							resultSet.getInt(4), TodoStatus.valueOf(resultSet.getString(5))));
				}
			}
		});
		return list;
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_TODO_TABLE);
	}

	@Override
	protected void update() {
	}
}