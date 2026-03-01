package com.github.cybellereaper.fusionBlock.nano;

import java.util.Objects;

/**
 * Defines a Nano power and how stamina is consumed when used.
 */
public final class NanoPower {
    private final String id;
    private final String name;
    private final NanoPowerMode mode;
    private final double staminaDrainPerSecond;
    private final double staminaCostOnTrigger;

    public NanoPower(String id, String name, NanoPowerMode mode, double staminaDrainPerSecond, double staminaCostOnTrigger) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.staminaDrainPerSecond = Math.max(0.0, staminaDrainPerSecond);
        this.staminaCostOnTrigger = Math.max(0.0, staminaCostOnTrigger);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NanoPowerMode getMode() {
        return mode;
    }

    public double getStaminaDrainPerSecond() {
        return staminaDrainPerSecond;
    }

    public double getStaminaCostOnTrigger() {
        return staminaCostOnTrigger;
    }
}
