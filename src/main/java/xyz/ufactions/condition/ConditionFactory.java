package xyz.ufactions.condition;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import xyz.ufactions.condition.conditions.Cloak;

public class ConditionFactory {

    private ConditionManager manager;

    public ConditionFactory(ConditionManager manager) {
        this.manager = manager;
    }

    public Condition custom(String reason, LivingEntity ent, LivingEntity source, Condition.ConditionType type, double duration, int multi, boolean extend, Material indMat, byte indData, boolean showIndicator) {
        return manager.addCondition(new Condition(manager, reason, ent, source, type, multi, (int) (20 * duration), extend, indMat, indData, showIndicator, false));
    }

    public Condition cloak(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean inform) {
        return manager.addCondition(new Cloak(manager, reason, ent, source, Condition.ConditionType.CLOAK, 0, (int) (20 * duration), extend, Material.GHAST_TEAR, (byte) 0, false));
    }

    public Condition blind(String reason, LivingEntity ent, LivingEntity source, double duration, int multi, boolean extend, boolean showIndicator, boolean ambient) {
        return manager.addCondition(new Condition(manager, reason, ent, source, Condition.ConditionType.BLINDNESS, multi, (int) (20 * duration), extend, Material.EYE_OF_ENDER, (byte) 0, showIndicator, ambient));
    }

    public Condition speed(String reason, LivingEntity ent, LivingEntity source, double duration, int multi, boolean extend, boolean showIndicator, boolean ambient) {
        return manager.addCondition(new Condition(manager, reason, ent, source, Condition.ConditionType.SPEED, multi, (int) (20 * duration), extend, Material.FEATHER, (byte) 0, showIndicator, ambient));
    }
}