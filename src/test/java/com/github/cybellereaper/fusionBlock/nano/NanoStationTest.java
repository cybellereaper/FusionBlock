package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NanoStationTest {

    @Test
    void stationCanSwapPowerAndEquippedNano() {
        NanoStation station = new NanoStation();
        NanoBook book = new NanoBook();
        Nano nanoA = new Nano();
        Nano nanoB = new Nano();
        NanoPower power = new NanoPower("Dash", NanoPowerMode.TRIGGERED, 8.0);

        book.addToRoster(nanoA);
        book.addToRoster(nanoB);
        station.swapEquippedNano(book, 0, nanoA);
        assertEquals(nanoA, book.getEquipped(0));

        station.swapEquippedNano(book, 0, nanoB);
        assertEquals(nanoB, book.getEquipped(0));

        station.swapPower(nanoB, power);
        assertEquals("Dash", nanoB.getSelectedPower().getName());
    }
}
