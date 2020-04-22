package xyz.ufactions.condition;

public class ConditionActive {

    private Condition condition;

    public ConditionActive(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        condition.apply();
    }
}