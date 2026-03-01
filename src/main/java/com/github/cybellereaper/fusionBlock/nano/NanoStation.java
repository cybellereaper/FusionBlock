package com.github.cybellereaper.fusionBlock.nano;

import java.util.Objects;

/**
 * Nano station interactions for swapping equipped nanos and powers.
 */
public class NanoStation {

    public void swapEquippedNano(NanoBook book, int slot, Nano nano) {
        Objects.requireNonNull(book, "book");
        Objects.requireNonNull(nano, "nano");
        book.equip(slot, nano);
    }

    public void swapPower(Nano nano, NanoPower newPower) {
        Objects.requireNonNull(nano, "nano");
        Objects.requireNonNull(newPower, "newPower");
        nano.setSelectedPower(newPower);
    }
}
