package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NanoEffectTypeTest {

    @Test
    void advantagedDamageInteractionRecoversStamina() {
        Nano receiver = new Nano();
        receiver.setType(NanoEffectType.BLASTONS);
        receiver.setStamina(50.0);

        NanoEffectType.ADAPTIUM.applyDamageInteraction(NanoEffectType.ADAPTIUM, receiver);

        assertEquals(51.5, receiver.getStamina());
    }

    @Test
    void identicalTypesDoNotModifyStamina() {
        Nano receiver = new Nano();
        receiver.setType(NanoEffectType.COSMIX);
        receiver.setStamina(40.0);

        NanoEffectType.COSMIX.applyDamageInteraction(NanoEffectType.COSMIX, receiver);

        assertEquals(40.0, receiver.getStamina());
    }

    @Test
    void typeAdvantagesMatchABCLoop() {
        assertEquals(1.25, NanoEffectType.ADAPTIUM.getDamageMultiplierAgainst(NanoEffectType.BLASTONS));
        assertEquals(1.25, NanoEffectType.BLASTONS.getDamageMultiplierAgainst(NanoEffectType.COSMIX));
        assertEquals(1.25, NanoEffectType.COSMIX.getDamageMultiplierAgainst(NanoEffectType.ADAPTIUM));
        assertEquals(0.75, NanoEffectType.ADAPTIUM.getDamageMultiplierAgainst(NanoEffectType.COSMIX));
    }

    @Test
    void advantageIndicatorsMapToUiStates() {
        assertEquals(NanoEffectType.AdvantageIndicator.ADVANTAGE,
                NanoEffectType.ADAPTIUM.getAdvantageIndicatorAgainst(NanoEffectType.BLASTONS));
        assertEquals(NanoEffectType.AdvantageIndicator.EQUAL,
                NanoEffectType.ADAPTIUM.getAdvantageIndicatorAgainst(NanoEffectType.ADAPTIUM));
        assertEquals(NanoEffectType.AdvantageIndicator.DISADVANTAGE,
                NanoEffectType.ADAPTIUM.getAdvantageIndicatorAgainst(NanoEffectType.COSMIX));
    }
}
