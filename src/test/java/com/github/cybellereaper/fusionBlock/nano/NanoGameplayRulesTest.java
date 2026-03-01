package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NanoGameplayRulesTest {

    @Test
    void typeAdvantageLoopMatchesGameRules() {
        assertTrue(NanoEffectType.ADAPTIUM.hasAdvantageOver(NanoEffectType.BLASTONS));
        assertTrue(NanoEffectType.BLASTONS.hasAdvantageOver(NanoEffectType.COSMIX));
        assertTrue(NanoEffectType.COSMIX.hasAdvantageOver(NanoEffectType.ADAPTIUM));

        assertEquals(CombatIndicator.ADVANTAGE,
                NanoEffectType.ADAPTIUM.indicatorAgainst(NanoEffectType.BLASTONS));
        assertEquals(CombatIndicator.EQUAL,
                NanoEffectType.ADAPTIUM.indicatorAgainst(NanoEffectType.ADAPTIUM));
        assertEquals(CombatIndicator.DISADVANTAGE,
                NanoEffectType.ADAPTIUM.indicatorAgainst(NanoEffectType.COSMIX));
    }

    @Test
    void acquisitionRequiresChoosingOneOfThreePowers() {
        Nano nano = new Nano();
        NanoPower p1 = new NanoPower("Speed", NanoPowerMode.PASSIVE, 1.5);
        NanoPower p2 = new NanoPower("Paralyze", NanoPowerMode.TRIGGERED, 15.0);
        NanoPower p3 = new NanoPower("Critical", NanoPowerMode.PASSIVE, 2.0);

        nano.acquireWithPowerSelection(List.of(p1, p2, p3), 1);

        assertEquals("Paralyze", nano.getSelectedPower().getName());
    }

    @Test
    void passiveAndTriggeredPowersDrainStaminaDifferently() {
        Nano nano = new Nano();
        nano.setStamina(100.0);
        nano.setSelectedPower(new NanoPower("Passive", NanoPowerMode.PASSIVE, 3.0));
        nano.summon();

        nano.tickSummonedDrain(2.0);
        assertEquals(95.0, nano.getStamina());

        nano.setSelectedPower(new NanoPower("Trigger", NanoPowerMode.TRIGGERED, 10.0));
        nano.tickSummonedDrain(2.0);
        assertEquals(93.0, nano.getStamina());

        nano.triggerSelectedPower();
        assertEquals(83.0, nano.getStamina());
    }

    @Test
    void gumballBoostRequiresTypeMatchAndLastsTenMinutes() {
        Nano nano = new Nano();
        nano.setType(NanoEffectType.COSMIX);
        Clock start = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

        nano.applyGumballBoost(NanoEffectType.COSMIX, start);
        assertTrue(nano.hasActiveGumballBoost(start));

        Clock nineMinutesLater = Clock.fixed(Instant.parse("2026-01-01T00:09:00Z"), ZoneOffset.UTC);
        assertTrue(nano.hasActiveGumballBoost(nineMinutesLater));

        Clock elevenMinutesLater = Clock.fixed(Instant.parse("2026-01-01T00:11:00Z"), ZoneOffset.UTC);
        assertFalse(nano.hasActiveGumballBoost(elevenMinutesLater));

        assertThrows(IllegalArgumentException.class,
                () -> nano.applyGumballBoost(NanoEffectType.ADAPTIUM, start));
    }

    @Test
    void corruptionAttackAdjustsStaminaByTypeRelationship() {
        CorruptionAttackResolver resolver = new CorruptionAttackResolver();
        Nano nano = new Nano();
        nano.setType(NanoEffectType.ADAPTIUM);

        nano.setStamina(50.0);
        resolver.apply(nano, NanoEffectType.BLASTONS, 10.0);
        assertEquals(60.0, nano.getStamina());

        nano.setStamina(50.0);
        resolver.apply(nano, NanoEffectType.COSMIX, 10.0);
        assertEquals(40.0, nano.getStamina());

        nano.setStamina(50.0);
        resolver.apply(nano, NanoEffectType.ADAPTIUM, 10.0);
        assertEquals(45.0, nano.getStamina());
    }
}
