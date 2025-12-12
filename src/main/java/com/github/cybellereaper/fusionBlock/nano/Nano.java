package com.github.cybellereaper.fusionBlock.nano;

import java.util.Objects;
import java.util.UUID;

public class Nano {
    private String id = UUID.randomUUID().toString();
    private String name = "blah blah";
    private NanoEffectType type = NanoEffectType.ADAPTIUM;
    private String desc = "blah blah information";
    private double stamina = 100.0;
    private boolean summoned = false;
    private long powerCooldownCycle = 0L;
    private double powerBoosted = 0.0;

    public Nano() {
    }

    public Nano(String id, String name, NanoEffectType type, String desc, double stamina, boolean summoned,
                long powerCooldownCycle, double powerBoosted) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.desc = Objects.requireNonNull(desc, "desc");
        this.stamina = stamina;
        this.summoned = summoned;
        this.powerCooldownCycle = powerCooldownCycle;
        this.powerBoosted = powerBoosted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public NanoEffectType getType() {
        return type;
    }

    public void setType(NanoEffectType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = Objects.requireNonNull(desc, "desc");
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = stamina;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    public long getPowerCooldownCycle() {
        return powerCooldownCycle;
    }

    public void setPowerCooldownCycle(long powerCooldownCycle) {
        this.powerCooldownCycle = powerCooldownCycle;
    }

    public double getPowerBoosted() {
        return powerBoosted;
    }

    public void setPowerBoosted(double powerBoosted) {
        this.powerBoosted = powerBoosted;
    }

    public void recoverStamina(double amount) {
        if (stamina >= 100.0) {
            return;
        }
        stamina = Math.min(100.0, stamina + amount);
    }

    public void damageStamina(double damage) {
        if (stamina <= 0.0) {
            summoned = false;
            return;
        }
        stamina -= damage;
        if (stamina <= 0.0) {
            summoned = false;
            stamina = 0.0;
        }
    }
}
