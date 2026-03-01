package com.github.cybellereaper.fusionBlock.nano;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a nano with stamina, type, and summon state.
 */
public class Nano {
    private static final double MAX_STAMINA = 100.0;
    private static final double MIN_STAMINA = 0.0;
    private String id = UUID.randomUUID().toString();
    private String name = "blah blah";
    private NanoEffectType type = NanoEffectType.ADAPTIUM;
    private String desc = "blah blah information";
    private double stamina = 100.0;
    private boolean summoned = false;
    private long powerCooldownCycle = 0L;
    private double powerBoosted = 0.0;
    private NanoPower selectedPower;
    private Instant gumballBoostExpiresAt;

    public Nano() {
    }

    /**
     * Creates a Nano with the provided state.
     */
    public Nano(String id, String name, NanoEffectType type, String desc, double stamina, boolean summoned,
                long powerCooldownCycle, double powerBoosted) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.desc = Objects.requireNonNull(desc, "desc");
        this.stamina = clampStamina(stamina);
        this.summoned = summoned;
        this.powerCooldownCycle = powerCooldownCycle;
        this.powerBoosted = powerBoosted;
    }

    /**
     * Returns the unique identifier for this nano.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this nano.
     */
    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    /**
     * Returns the nano name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the nano name.
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Returns the nano effect type.
     */
    public NanoEffectType getType() {
        return type;
    }

    /**
     * Sets the nano effect type.
     */
    public void setType(NanoEffectType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    /**
     * Returns the description.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the description.
     */
    public void setDesc(String desc) {
        this.desc = Objects.requireNonNull(desc, "desc");
    }

    /**
     * Returns the current stamina value.
     */
    public double getStamina() {
        return stamina;
    }

    /**
     * Sets the stamina, clamped to valid bounds.
     */
    public void setStamina(double stamina) {
        this.stamina = clampStamina(stamina);
    }

    /**
     * Returns whether the nano is summoned.
     */
    public boolean isSummoned() {
        return summoned;
    }

    /**
     * Sets whether the nano is summoned.
     */
    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    /**
     * Returns the current power cooldown cycle.
     */
    public long getPowerCooldownCycle() {
        return powerCooldownCycle;
    }

    /**
     * Sets the current power cooldown cycle.
     */
    public void setPowerCooldownCycle(long powerCooldownCycle) {
        this.powerCooldownCycle = powerCooldownCycle;
    }

    /**
     * Returns the power boost amount.
     */
    public double getPowerBoosted() {
        return powerBoosted;
    }

    /**
     * Sets the power boost amount.
     */
    public void setPowerBoosted(double powerBoosted) {
        this.powerBoosted = powerBoosted;
    }

    public NanoPower getSelectedPower() {
        return selectedPower;
    }

    public void setSelectedPower(NanoPower selectedPower) {
        this.selectedPower = selectedPower;
    }

    public Instant getGumballBoostExpiresAt() {
        return gumballBoostExpiresAt;
    }

    /**
     * Acquires this nano by selecting one of three offered powers.
     */
    public void acquireWithPowerSelection(List<NanoPower> offeredPowers, int selectedIndex) {
        Objects.requireNonNull(offeredPowers, "offeredPowers");
        if (offeredPowers.size() != 3) {
            throw new IllegalArgumentException("Exactly three powers must be offered");
        }
        if (selectedIndex < 0 || selectedIndex >= offeredPowers.size()) {
            throw new IllegalArgumentException("selectedIndex out of range");
        }
        setSelectedPower(offeredPowers.get(selectedIndex));
    }

    /**
     * Summons this nano if it has remaining stamina.
     */
    public boolean summon() {
        if (stamina <= MIN_STAMINA) {
            summoned = false;
            return false;
        }
        summoned = true;
        return true;
    }

    public void unsummon() {
        summoned = false;
    }

    /**
     * Applies baseline summon drain and passive power drain for one tick.
     */
    public void tickSummonedDrain(double baseDrainPerTick) {
        if (!summoned || baseDrainPerTick < 0.0) {
            return;
        }

        double drain = baseDrainPerTick;
        if (selectedPower != null) {
            drain += selectedPower.drainForTick();
        }
        damageStamina(drain);
    }

    /**
     * Triggers the selected power and drains stamina if the power is triggered.
     */
    public void triggerSelectedPower() {
        if (!summoned || selectedPower == null) {
            return;
        }
        damageStamina(selectedPower.drainForTrigger());
    }

    /**
     * Increases stamina by the requested amount, clamped to the max.
     */
    public void recoverStamina(double amount) {
        if (amount <= 0.0 || stamina >= MAX_STAMINA) {
            return;
        }
        stamina = clampStamina(stamina + amount);
    }

    /**
     * Decreases stamina by the requested damage and updates summon state.
     */
    public void damageStamina(double damage) {
        if (stamina <= MIN_STAMINA) {
            summoned = false;
            return;
        }
        if (damage <= 0.0) {
            return;
        }
        stamina = clampStamina(stamina - damage);
        if (stamina <= MIN_STAMINA) {
            summoned = false;
        }
    }

    /**
     * Applies a type-matching gumball boost for 10 minutes.
     */
    public void applyGumballBoost(NanoEffectType gumballType, Clock clock) {
        Objects.requireNonNull(gumballType, "gumballType");
        Objects.requireNonNull(clock, "clock");
        if (gumballType != type) {
            throw new IllegalArgumentException("Gumball type must match nano type");
        }

        gumballBoostExpiresAt = Instant.now(clock).plus(Duration.ofMinutes(10));
    }

    public boolean hasActiveGumballBoost(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return gumballBoostExpiresAt != null && Instant.now(clock).isBefore(gumballBoostExpiresAt);
    }

    private double clampStamina(double value) {
        return Math.max(MIN_STAMINA, Math.min(MAX_STAMINA, value));
    }
}
