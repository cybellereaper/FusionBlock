package com.github.cybellereaper.fusionBlock.nano;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Player nano roster with three active equip slots.
 */
public class NanoBook {
    public static final int ACTIVE_SLOTS = 3;

    private final List<Nano> roster = new ArrayList<>();
    private final Nano[] equipped = new Nano[ACTIVE_SLOTS];

    public void addToRoster(Nano nano) {
        roster.add(Objects.requireNonNull(nano, "nano"));
    }

    public List<Nano> getRoster() {
        return Collections.unmodifiableList(roster);
    }

    public void equip(int slot, Nano nano) {
        if (slot < 0 || slot >= ACTIVE_SLOTS) {
            throw new IllegalArgumentException("slot must be between 0 and 2");
        }
        Objects.requireNonNull(nano, "nano");
        if (!roster.contains(nano)) {
            throw new IllegalArgumentException("nano must be in roster before equipping");
        }
        equipped[slot] = nano;
    }

    public Nano getEquipped(int slot) {
        if (slot < 0 || slot >= ACTIVE_SLOTS) {
            throw new IllegalArgumentException("slot must be between 0 and 2");
        }
        return equipped[slot];
    }

    public boolean summonFromSlot(int slot) {
        Nano nano = getEquipped(slot);
        if (nano == null) {
            return false;
        }
        return nano.summon();
    }

    public void unsummonSlot(int slot) {
        Nano nano = getEquipped(slot);
        if (nano != null) {
            nano.unsummon();
        }
    }
}
