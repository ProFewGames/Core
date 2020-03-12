package xyz.ufactions.help.data;

import org.bukkit.OfflinePlayer;

public class Report {

    private int id;
    private OfflinePlayer reporter;
    private OfflinePlayer reported;
    private String reason;

    public Report(int id, OfflinePlayer reporter, OfflinePlayer reported, String reason) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
    }

    public Report(OfflinePlayer reporter, OfflinePlayer reported, String reason) {
        this(-1, reporter, reported, reason);
    }

    public int getId() {
        return id;
    }

    public OfflinePlayer getReporter() {
        return reporter;
    }

    public void setReporter(OfflinePlayer reporter) {
        this.reporter = reporter;
    }

    public OfflinePlayer getReported() {
        return reported;
    }

    public void setReported(OfflinePlayer reported) {
        this.reported = reported;
    }

    public String getReason() {
        return reason;
    }
}