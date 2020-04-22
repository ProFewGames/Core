package xyz.ufactions.damage;

public class DamageChange {

    private String source;
    private String reason;
    private double modifier;
    private boolean useReason;

    public DamageChange(String source, String reason, double modifier, boolean useReason) {
        this.source = source;
        this.reason = reason;
        this.modifier = modifier;
        this.useReason = useReason;
    }

    public String getSource() {
        return source;
    }

    public String getReason() {
        return reason;
    }

    public double getDamage() {
        return modifier;
    }

    public boolean useReason() {
        return useReason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}