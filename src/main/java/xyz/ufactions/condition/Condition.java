package xyz.ufactions.condition;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Condition {

    public enum ConditionType {
        CLOAK,

        SPEED,
        BLINDNESS
    }

    protected ConditionManager manager;
    protected long time;

    protected String reason;

    protected String informOn;
    protected String informOff;

    protected LivingEntity ent;
    protected LivingEntity source;

    protected ConditionType type;
    protected int mult;
    protected int ticks;
    protected int ticksTotal;
    protected boolean ambient;

    protected Material indicatorType;
    protected byte indicatorData;

    protected boolean add = false;
    protected boolean live = false;

    protected boolean showIndicator = false;

    public Condition(ConditionManager manager, String reason, LivingEntity ent, LivingEntity source, ConditionType type, int multi, int ticks, boolean add, Material visualType, byte visualData, boolean showIndicator, boolean ambient) {
        this.manager = manager;
        this.time = System.currentTimeMillis();

        this.reason = reason;

        this.ent = ent;
        this.source = source;

        this.type = type;
        this.mult = multi;
        this.ticks = ticks;
        this.ticksTotal = ticks;
        this.ambient = ambient;

        this.indicatorType = visualType;
        this.indicatorData = visualData;
        this.showIndicator = showIndicator;

        this.add = add;

        //Live if NOT Additive
        this.live = !add;
    }

    public boolean tick() {
        if (live && ticks > 0) ticks--;

        return isExpired();
    }

    public void onConditionAdd() {
    }

    public void apply() {
        live = true;

        add();
    }

    public void add() {
        try {
            PotionEffectType type = PotionEffectType.getByName(this.type.toString());

            //Remove
            ent.removePotionEffect(type);

            //Add
            if (ticks == -1)
                ent.addPotionEffect(new PotionEffect(type, 72000, mult, ambient), true);
            else
                ent.addPotionEffect(new PotionEffect(type, ticks, mult, ambient), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove() {
        PotionEffectType type = PotionEffectType.getByName(this.type.toString());
        ent.removePotionEffect(type);
    }

    public Material getIndicatorMaterial() {
        return indicatorType;
    }

    public byte getIndicatorData() {
        return indicatorData;
    }

    public LivingEntity getEnt() {
        return ent;
    }

    public LivingEntity getSource() {
        return source;
    }

    public boolean isAdd() {
        return add;
    }

    public ConditionType getType() {
        return type;
    }

    public int getMult() {
        return mult;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public int getTicks() {
        return ticks;
    }

    public int getTicksTotal() {
        return ticksTotal;
    }

    public String getReason() {
        return reason;
    }

    public long getTime() {
        return time;
    }

    public void expire() {
        ticks = 0;

        remove();
    }

    public void restart() {
        ticks = ticksTotal;
    }

    public boolean isBetterOrEqual(Condition other, boolean additive) {
        if (this.getMult() > other.getMult())
            return true;

        if (this.getMult() < other.getMult())
            return false;

        if (additive)
            return true;

        if (this.getTicks() >= other.getTicks())
            return true;

        return false;
    }

    public boolean isVisible() {
        return showIndicator;
    }

    public boolean isExpired() {
        if (ticks == -1) return false;
        return ticks <= 0;
    }

    public ConditionManager getManager() {
        return manager;
    }

    public String getInformOn() {
        return informOn;
    }

    public String getInformOff() {
        return informOff;
    }

    public void modifyTicks(int amount) {
        ticks += amount;
        ticksTotal += amount;
    }

    public void modifyMult(int i) {
        mult = Math.max(0, mult + i);
    }
}