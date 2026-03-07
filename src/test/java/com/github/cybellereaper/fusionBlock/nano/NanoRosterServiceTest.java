package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NanoRosterServiceTest {

    @Test
    void nanoBookCanEquipOnlyThreeActiveNanos() {
        NanoRosterService service = new NanoRosterService();
        acquire(service, nano("n1", NanoEffectType.ADAPTIUM));
        acquire(service, nano("n2", NanoEffectType.BLASTONS));
        acquire(service, nano("n3", NanoEffectType.COSMIX));
        acquire(service, nano("n4", NanoEffectType.COSMIX));

        service.equipNano("n1");
        service.equipNano("n2");
        service.equipNano("n3");

        assertThrows(IllegalStateException.class, () -> service.equipNano("n4"));
        assertEquals(3, service.getEquippedNanoIds().size());
    }

    @Test
    void summonAndUpdateDrainStaminaUntilUnsummoned() {
        NanoRosterService service = new NanoRosterService();
        Nano nano = nano("n1", NanoEffectType.ADAPTIUM);
        nano.setStamina(2.0);
        acquire(service, nano);
        service.equipNano("n1");

        service.summon("n1");
        service.updateSummon(1.0);

        assertTrue(nano.isSummoned());
        service.updateSummon(2.0);
        assertFalse(nano.isSummoned());
        assertNull(service.getSummonedNanoId());
    }

    @Test
    void triggeredPowerConsumesLargeChunkOfStamina() {
        NanoRosterService service = new NanoRosterService();
        Nano nano = nano("n1", NanoEffectType.ADAPTIUM);
        service.acquireNano(nano, List.of(
                passive("p1", 1.0),
                triggered("p2", 30.0),
                passive("p3", 2.0)
        ), 1);
        service.equipNano("n1");
        service.summon("n1");

        service.triggerPower();

        assertEquals(70.0, nano.getStamina());
    }

    @Test
    void powerSwapRequiresNanoStation() {
        NanoRosterService service = new NanoRosterService();
        Nano nano = nano("n1", NanoEffectType.ADAPTIUM);
        service.acquireNano(nano, List.of(
                passive("p1", 1.0),
                triggered("p2", 30.0),
                passive("p3", 2.0)
        ), 0);

        assertThrows(IllegalStateException.class, () -> service.swapPowerAtNanoStation("n1", "p2", false));

        service.swapPowerAtNanoStation("n1", "p2", true);
        assertEquals("p2", service.getSelectedPower("n1").getId());
    }

    @Test
    void gumballBoostRequiresMatchingTypeAndExpiresAfterTenMinutes() {
        MutableClock clock = new MutableClock(Instant.parse("2025-01-01T00:00:00Z"));
        NanoRosterService service = new NanoRosterService(clock);
        Nano nano = nano("n1", NanoEffectType.ADAPTIUM);
        acquire(service, nano);

        assertThrows(IllegalArgumentException.class, () -> service.applyGumball("n1", NanoEffectType.COSMIX));

        service.applyGumball("n1", NanoEffectType.ADAPTIUM);
        assertEquals(0.20, nano.getPowerBoosted());

        clock.plusSeconds(600);
        service.refreshBoosts();
        assertEquals(0.0, nano.getPowerBoosted());
    }

    @Test
    void combatLogicUsesAbcAdvantageLoop() {
        NanoRosterService service = new NanoRosterService();

        assertEquals(1.25, service.getCombatDamageMultiplier(NanoEffectType.ADAPTIUM, NanoEffectType.BLASTONS));
        assertEquals(NanoEffectType.AdvantageIndicator.ADVANTAGE,
                service.getCombatIndicatorAgainst(NanoEffectType.ADAPTIUM, NanoEffectType.BLASTONS));
    }

    @Test
    void acquireNanoRejectsDuplicatePowerIds() {
        NanoRosterService service = new NanoRosterService();

        assertThrows(IllegalArgumentException.class, () -> service.acquireNano(nano("n1", NanoEffectType.ADAPTIUM), List.of(
                passive("same", 1.0),
                triggered("same", 20.0),
                passive("p3", 2.0)
        ), 0));
    }

    @Test
    void swapPowerRejectsUnknownPowerId() {
        NanoRosterService service = new NanoRosterService();
        acquire(service, nano("n1", NanoEffectType.ADAPTIUM));

        assertThrows(IllegalArgumentException.class, () -> service.swapPowerAtNanoStation("n1", "unknown", true));
    }

    @Test
    void nullIdsAreRejectedEarly() {
        NanoRosterService service = new NanoRosterService();

        assertThrows(NullPointerException.class, () -> service.equipNano(null));
        assertThrows(NullPointerException.class, () -> service.swapPowerAtNanoStation("n1", null, true));
    }

    private static void acquire(NanoRosterService service, Nano nano) {
        service.acquireNano(nano, List.of(
                passive("p1", 1.0),
                triggered("p2", 25.0),
                passive("p3", 2.0)
        ), 0);
    }

    private static Nano nano(String id, NanoEffectType type) {
        return new Nano(id, "test", type, "desc", 100.0, false, 0L, 0.0);
    }

    private static NanoPower passive(String id, double drainPerSecond) {
        return new NanoPower(id, id, NanoPowerMode.PASSIVE, drainPerSecond, 0.0);
    }

    private static NanoPower triggered(String id, double cost) {
        return new NanoPower(id, id, NanoPowerMode.TRIGGERED, 0.0, cost);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void plusSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }
    }
}
