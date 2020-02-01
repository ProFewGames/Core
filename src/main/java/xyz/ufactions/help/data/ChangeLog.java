package xyz.ufactions.help.data;

public class ChangeLog {

    private int id;
    private String change;

    public ChangeLog(int id, String change) {
        this.id = id;
        this.change = change;
    }

    public int getId() {
        return id;
    }

    public String getChange() {
        return change;
    }
}