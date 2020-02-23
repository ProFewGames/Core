package xyz.ufactions.tablist.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.column.ColumnVarChar;

public class TablistRepository extends RepositoryBase {

	private final String CREATE_TABLIST_TABLE = "CREATE TABLE IF NOT EXISTS `tablist` (`id` INT AUTO_INCREMENT NOT NULL, `group` VARCHAR(50), `tab` VARCHAR(32), PRIMARY KEY(id));";;
	private final String SELECT_TABLIST = "SELECT * FROM `tablist`";
	private final String CREATE_TABLIST = "INSERT INTO `tablist` (`group`, `tab`) VALUES (?, ?);";
	private final String DELETE_TABLIST = "DELETE FROM `tablist` WHERE `group`=?";
	private final String UPDATE_TABLIST = "UPDATE `tablist` SET `tab`=? WHERE `group`=?;";

	public TablistRepository(JavaPlugin plugin) {
		super(plugin, DBPool.MAIN);
	}

	public void updateTablist(String group, String tab) {
		executeUpdate(UPDATE_TABLIST, new ColumnVarChar("tab", 32, tab), new ColumnVarChar("group", 50, group));
	}

	public void createTablist(String group, String tab) {
		executeUpdate(CREATE_TABLIST, new ColumnVarChar("group", 50, group), new ColumnVarChar("tab", 32, tab));
	}

	public void deleteTablist(String group) {
		executeUpdate(DELETE_TABLIST, new ColumnVarChar("group", 50, group));
	}

	public HashMap<String, String> getTablist() {
		HashMap<String, String> tablist = new HashMap<>();
		executeQuery(SELECT_TABLIST, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					tablist.put(resultSet.getString(2), resultSet.getString(3));
				}
			}
		});
		return tablist;
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_TABLIST_TABLE);
	}

	@Override
	protected void update() {
	}
}