package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NanoBookTest {

    @Test
    void onlyThreeEquipSlotsAreAllowed() {
        NanoBook book = new NanoBook();
        Nano nano = new Nano();
        book.addToRoster(nano);

        book.equip(0, nano);
        book.equip(1, nano);
        book.equip(2, nano);

        assertThrows(IllegalArgumentException.class, () -> book.equip(3, nano));
    }

    @Test
    void summonAndUnsummonFromSlotUsesEquippedNano() {
        NanoBook book = new NanoBook();
        Nano nano = new Nano();
        nano.setStamina(20.0);
        book.addToRoster(nano);
        book.equip(1, nano);

        assertTrue(book.summonFromSlot(1));
        assertTrue(nano.isSummoned());

        book.unsummonSlot(1);
        assertFalse(nano.isSummoned());
    }
}
