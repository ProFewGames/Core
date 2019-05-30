package xyz.ufactions.tags.repository;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.column.ColumnInt;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.tags.event.TokenUpdateEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TitleRepository extends RepositoryBase {

	private final String CREATE_TOKENS_TABLE = "CREATE TABLE IF NOT EXISTS `player_tag_tokens` (`player_uuid` VARCHAR(50) NOT NULL, `tokens` INT);";
	private final String GET_TOKENS_STATEMENT = "SELECT * FROM `player_tag_tokens` WHERE `player_uuid` = ?;";
	private final String INSERT_TOKENS_STATEMENT = "INSERT INTO `player_tag_tokens` (`player_uuid`, `tokens`) VALUES (?, ?);";
	private final String DELETE_TOKENS_STATEMENT = "DELETE FROM `player_tag_tokens` WHERE `player_uuid` = ?;";

	private final String CREATE_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS `player_tags` (`player_uuid` VARCHAR(50) NOT NULL, `player_tag` VARCHAR(50) NOT NULL);";
	private final String GET_TAGS_STATEMENT = "SELECT * FROM `player_tags` WHERE `player_uuid` = ?;";
	private final String ADD_TAGS_STATEMENT = "INSERT INTO `player_tags` (`player_uuid`, `player_tag`) VALUES (?, ?);";

	private final String CREATE_PLAYER_TAGS_TABLE = "CREATE TABLE IF NOT EXISTS `player_active_tag` (`player_uuid` VARCHAR(50) NOT NULL, `player_tag` VARCHAR(50) NOT NULL);";
	private final String GET_TAG_STATEMENT = "SELECT * FROM `player_active_tag` WHERE `player_uuid` = ?;";
	private final String INSERT_TAG_STATEMENT = "INSERT INTO `player_active_tag` (`player_uuid`, `player_tag`) VALUES (?, ?);";
	private final String DELETE_TAG_STATEMENT = "DELETE FROM `player_active_tag` WHERE `player_uuid` = ?;";

	public TitleRepository(JavaPlugin plugin) {
		super(plugin, DBPool.MAIN);
	}

	public int getTokens(UUID uuid) {
		final List<Integer> tokens = new ArrayList<>();
		executeQuery(GET_TOKENS_STATEMENT, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					tokens.add(resultSet.getInt(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		return tokens.isEmpty() ? 0 : tokens.get(0);
	}

	public void updateTokens(UUID uuid, int amount) {
		final int tokens = getTokens(uuid);
		if (tokens + amount < 0) {
			amount = -tokens;
		}
		executeUpdate(DELETE_TOKENS_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		executeUpdate(INSERT_TOKENS_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnInt("tokens", Integer.valueOf(tokens + amount)));
		Bukkit.getServer().getPluginManager().callEvent(new TokenUpdateEvent(uuid, tokens + amount));
	}

	public void saveTag(UUID uuid, String tag) {
		executeUpdate(DELETE_TAG_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		if (tag == null)
			return;
		if (tag.equals(""))
			return;
		executeUpdate(INSERT_TAG_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnVarChar("player_tag", 50, tag));
	}

	public List<String> getTags(UUID uuid) {
		final List<String> tags = new ArrayList<>();
		executeQuery(GET_TAGS_STATEMENT, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					tags.add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		return tags;
	}

	public String getTag(UUID uuid) {
		final List<String> tags = new ArrayList<>();
		executeQuery(GET_TAG_STATEMENT, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					tags.add(resultSet.getString(2));
				}
			}
		}, new ColumnVarChar("player_uuid", 50, uuid.toString()));
		String tag = tags.isEmpty() ? "" : tags.get(0);
		return tag;
	}

	public void addTag(UUID uuid, String tag) {
		tag = tag.toLowerCase();
		executeUpdate(ADD_TAGS_STATEMENT, new ColumnVarChar("player_uuid", 50, uuid.toString()),
				new ColumnVarChar("player_tag", 50, tag));
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_TAGS_TABLE);
		executeUpdate(CREATE_PLAYER_TAGS_TABLE);
		executeUpdate(CREATE_TOKENS_TABLE);
	}

	@Override
	protected void update() {
	}
}