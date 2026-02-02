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
    void recoverStaminaIgnoresNonPositiveAmounts() {
        Nano nano = new Nano();
        nano.setStamina(50.0);

        nano.recoverStamina(0.0);
        assertEquals(50.0, nano.getStamina());

        nano.recoverStamina(-5.0);
        assertEquals(50.0, nano.getStamina());
    }

    @Test
    void setStaminaClampsToValidRange() {
        Nano nano = new Nano();

        nano.setStamina(150.0);
        assertEquals(100.0, nano.getStamina());

        nano.setStamina(-10.0);
        assertEquals(0.0, nano.getStamina());
    }

    @Test
    void constructorClampsStaminaToValidRange() {
        Nano nano = new Nano("id", "name", NanoEffectType.COSMIX, "desc", 150.0, true, 0L, 0.0);

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

    @Test
    void damageStaminaIgnoresNonPositiveAmounts() {
        Nano nano = new Nano();
        nano.setStamina(50.0);
        nano.setSummoned(true);

        nano.damageStamina(0.0);
        assertEquals(50.0, nano.getStamina());
        assertTrue(nano.isSummoned());

        nano.damageStamina(-5.0);
        assertEquals(50.0, nano.getStamina());
        assertTrue(nano.isSummoned());
    }

    @Test
    void damageStaminaDisablesSummonWhenAlreadyEmpty() {
        Nano nano = new Nano();
        nano.setStamina(0.0);
        nano.setSummoned(true);

        nano.damageStamina(0.0);

        assertFalse(nano.isSummoned());
    }
}
