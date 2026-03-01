package com.github.cybellereaper.fusionBlock.nano;

import java.util.Objects;

/**
 * Nano power metadata with stamina drain behavior.
 */
public final class NanoPower {
    private final String name;
    private final NanoPowerMode mode;
    private final double staminaCost;

    public NanoPower(String name, NanoPowerMode mode, double staminaCost) {
        this.name = Objects.requireNonNull(name, "name");
        this.mode = Objects.requireNonNull(mode, "mode");
        if (staminaCost < 0.0) {
            throw new IllegalArgumentException("staminaCost must be >= 0");
        }
        this.staminaCost = staminaCost;
    }

    public String getName() {
        return name;
    }

    public NanoPowerMode getMode() {
        return mode;
    }

    public double getStaminaCost() {
        return staminaCost;
    }

    public double drainForTick() {
        return mode == NanoPowerMode.PASSIVE ? staminaCost : 0.0;
    }

    public double drainForTrigger() {
        return mode == NanoPowerMode.TRIGGERED ? staminaCost : 0.0;
    }
}
