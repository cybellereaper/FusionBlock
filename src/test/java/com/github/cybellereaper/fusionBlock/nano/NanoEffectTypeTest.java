package com.github.cybellereaper.fusionBlock.nano;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NanoEffectTypeTest {

    @Test
    void cosmixDamagingBlastonsRecoversStamina() {
        Nano receiver = new Nano();
        receiver.setType(NanoEffectType.BLASTONS);
        receiver.setStamina(50.0);

        NanoEffectType.ADAPTIUM.applyDamageInteraction(NanoEffectType.COSMIX, receiver);

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
}
