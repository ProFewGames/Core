package xyz.ufactions.help.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.help.data.ChangeLog;

public class ChangelogRepository extends RepositoryBase {

	private final String CREATE_CHANGELOG_TABLE = "CREATE TABLE IF NOT EXISTS `changelog` (`id` INT AUTO_INCREMENT NOT NULL, `change` VARCHAR(100), PRIMARY KEY(id));";
	private final String RETREIVE_CHANGES = "SELECT * FROM `changelog` WHERE 1;";
	private final String INSERT_CHANGE = "INSERT INTO `changelog` (`change`) VALUES (?);";

	public ChangelogRepository(JavaPlugin plugin) {
		super(plugin, DBPool.MAIN);
	}

	public final void createChange(String change) {
		executeUpdate(INSERT_CHANGE, new ColumnVarChar("change", 100, change));
	}

	public final List<ChangeLog> getChanges() {
		return getChanges(-1);
	}

	public final List<ChangeLog> getChanges(final int amount) {
		final List<ChangeLog> list = new ArrayList<>();
		executeQuery(RETREIVE_CHANGES, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				int i = amount;
				while (resultSet.next() && (i--  != 0 || amount > 0)) {
					list.add(new ChangeLog(resultSet.getInt(1), resultSet.getString(2)));
				}
			}
		});
		return list;
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_CHANGELOG_TABLE);
	}

	@Override
	protected void update() {
	}
}