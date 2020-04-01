package xyz.ufactions.motd.database;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.column.ColumnInt;
import xyz.ufactions.database.column.ColumnVarChar;

import java.util.HashMap;

public class MOTDRepository extends RepositoryBase {

    private final String CREATE = "CREATE TABLE IF NOT EXISTS motd (id INT AUTO_INCREMENT, motd VARCHAR(1028), PRIMARY KEY(id));";
    private final String GET = "SELECT * FROM motd;";
    private final String INSERT = "INSERT INTO motd (motd) VALUES (?);";
    private final String DELETE = "DELETE FROM motd WHERE id=?;";

    public MOTDRepository(JavaPlugin plugin) {
        super(plugin, DBPool.MAIN);
    }

    public HashMap<Integer, String> getMOTDs() {
        HashMap<Integer, String> map = new HashMap<>();
        executeQuery(GET, resultSet -> {
            while (resultSet.next()) {
                map.put(resultSet.getInt("id"), resultSet.getString("motd"));
            }
        });
        return map;
    }

    public void addMOTD(String motd) {
        executeUpdate(INSERT, new ColumnVarChar("motd", 1028, motd));
    }

    public void deleteMOTD(int id) {
        executeUpdate(DELETE, new ColumnInt("id", id));
    }

    @Override
    protected void initialize() {
        executeUpdate(CREATE);
    }

    @Override
    protected void update() {
    }
}