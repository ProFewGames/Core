package xyz.ufactions.coins.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.ResultSetCallable;
import xyz.ufactions.database.SourceType;
import xyz.ufactions.database.column.ColumnInt;
import xyz.ufactions.database.column.ColumnVarChar;

public class CoinRepository extends RepositoryBase {

	private static String CREATE_COINS_TABLE = "CREATE TABLE IF NOT EXISTS player_coins (uuid VARCHAR(100), coins INT, PRIMARY KEY (uuid));";
	private static String RETRIEVE_COINS = "SELECT coins FROM player_coins WHERE uuid = ?;";
	private static String INSERT_COINS = "INSERT INTO player_coins (uuid, coins) VALUES (?, ?);";
	private static String UPDATE_COINS = "UPDATE player_coins SET coins = ? WHERE uuid = ?;";

	public CoinRepository(JavaPlugin plugin) {
		super(plugin, DBPool.getSource(SourceType.NETWORK));
	}

	public int getCoins(UUID uuid) {
		final List<Integer> coins = new ArrayList<>();
		executeQuery(RETRIEVE_COINS, new ResultSetCallable() {

			@Override
			public void processResultSet(ResultSet resultSet) throws SQLException {

				while (resultSet.next()) {
					coins.add(resultSet.getInt(1));
				}
			}
		}, new ColumnVarChar("uuid", 100, uuid.toString()));
		if (coins.isEmpty()) {
			executeUpdate(INSERT_COINS, new ColumnVarChar("uuid", 100, uuid.toString()), new ColumnInt("coins", 0));
			coins.add(0);
		}
		return coins.get(0);
	}

	public void addCoins(UUID uuid, int coins) {
		if (getCoins(uuid) + coins < 0) {
			coins = -getCoins(uuid);
		}
		executeUpdate(UPDATE_COINS, new ColumnInt("coins", getCoins(uuid) + coins),
				new ColumnVarChar("uuid", 100, uuid.toString()));
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_COINS_TABLE);
	}

	@Override
	protected void update() {
	}
}