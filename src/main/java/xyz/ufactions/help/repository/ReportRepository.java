package xyz.ufactions.help.repository;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.database.DBPool;
import xyz.ufactions.database.RepositoryBase;
import xyz.ufactions.database.column.ColumnInt;
import xyz.ufactions.database.column.ColumnVarChar;
import xyz.ufactions.help.data.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepository extends RepositoryBase {

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `reports` (`id` INT AUTO_INCREMENT, `reporter` VARCHAR(36), `reported` VARCHAR(36), `reason` VARCHAR(50), PRIMARY KEY(id));";
    private final String SELECT_ALL = "SELECT * FROM `reports`;";
    private final String SELECT_BY_REPOTED = "SELECT * FROM `reports` WHERE `reported` = ?;";
    private final String DELETE_BY_REPORTED = "DELETE FROM `reports` WHERE `reported` = ?;";
    private final String DELETE_BY_ID = "DELETE FROM `reports` WHERE `id` = ?;";
    private final String INSERT = "INSERT INTO `reports` (`reporter`, `reported`, `reason`) VALUES (?, ?, ?);";

    public ReportRepository(JavaPlugin plugin) {
        super(plugin, DBPool.MAIN);
    }

    public void deleteReports(OfflinePlayer player) {
        executeUpdate(DELETE_BY_REPORTED, new ColumnVarChar("reported", 16, player.getUniqueId().toString()));
    }

    public void deleteReport(int id) {
        executeUpdate(DELETE_BY_ID, new ColumnInt("id", id));
    }

    public List<Report> getReports(OfflinePlayer reported) {
        List<Report> list = new ArrayList<>();
        executeQuery(SELECT_BY_REPOTED, resultSet -> {
            while (resultSet.next()) {
                list.add(new Report(resultSet.getInt("id"), Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("reporter"))), Bukkit.getOfflinePlayer(UUID.fromString("reported")), resultSet.getString("reason")));
            }
        }, new ColumnVarChar("reported", 36, reported.getUniqueId().toString()));
        return list;
    }

    public List<Report> getReports() {
        List<Report> list = new ArrayList<>();
        executeQuery(SELECT_ALL, resultSet -> {
            while (resultSet.next()) {
                list.add(new Report(resultSet.getInt("id"), Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("reporter"))), Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("reported"))), resultSet.getString("reason")));
            }
        });
        return list;
    }

    public int createReport(Report report) {
        List<Integer> list = new ArrayList<>();
        executeInsert(INSERT, resultSet -> {
            while (resultSet.next()) {/*
                System.out.println(resultSet);
                System.out.println(resultSet.getString("reporter"));
                list.add(resultSet.getInt("id"));*/
            }
        }, new ColumnVarChar("reporter", 36, report.getReporter().getUniqueId().toString()), new ColumnVarChar("reported", 36, report.getReported().getUniqueId().toString()), new ColumnVarChar("reason", 50, report.getReason()));
        return (list.isEmpty() ? -1 : list.get(0));
    }

    @Override
    protected void initialize() {
        executeUpdate(CREATE_TABLE);
    }

    @Override
    protected void update() {
    }
}