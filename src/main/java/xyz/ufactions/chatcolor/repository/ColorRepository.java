package xyz.ufactions.chatcolor.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.SourceType;
import xyz.ufactions.database.column.ColumnVarChar;

public class ColorRepository extends RepositoryBase {

	private final String CREATE_COLOR_TABLE = "CREATE TABLE IF NOT EXISTS `player_colors` (`player_uuid` VARCHAR(50), `player_color` VARCHAR(50), PRIMARY KEY(`player_uuid`));";

	private final String DELETE_COLOR_STATEMENT = "DELETE FROM `player_colors` WHERE `player_uuid` = ?;";
	private final String INSERT_COLOR_STATEMENT = "INSERT INTO `player_colors` (`player_uuid`, `player_color`) VALUES (?, ?);";
	private final String GET_COLOR_STATEMENT = "SELECT * FROM `player_colors` WHERE `player_uuid` = ?;";

	public ColorRepository(JavaPlugin plugin) {
		super(plugin, DBPool.getSource(SourceType.NETWORK));
	}

	public ChatColor getColor(UUID uuid) {
		final List<String> colors = new ArrayList<>();
		executeQuery(GET_COLOR_STATEMENT, new ResultSetCallable() {

			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					colors.add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		return colors.isEmpty() ? ChatColor.WHITE : ChatColor.valueOf(colors.get(0).toUpperCase());
	}

	public void setColor(UUID uuid, ChatColor color) {
		executeUpdate(DELETE_COLOR_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		executeUpdate(INSERT_COLOR_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnVarChar("player_color", 50, color.name().toUpperCase()));
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_COLOR_TABLE);
	}

	@Override
	protected void update() {
	}
}