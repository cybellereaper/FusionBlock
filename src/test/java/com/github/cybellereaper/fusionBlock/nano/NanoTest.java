package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NanoTest {

    @Test
    void recoverStaminaStopsAtCap() {
        Nano nano = new Nano();
        nano.setStamina(95.0);

        nano.recoverStamina(10.0);

        assertEquals(100.0, nano.getStamina());
    }

    @Test
    void damageStaminaDisablesSummonWhenDepleted() {
        Nano nano = new Nano();
        nano.setStamina(5.0);
        nano.setSummoned(true);

        nano.damageStamina(10.0);

        assertEquals(0.0, nano.getStamina());
        assertFalse(nano.isSummoned());
    }
}
