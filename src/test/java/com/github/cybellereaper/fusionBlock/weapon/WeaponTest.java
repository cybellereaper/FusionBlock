package com.github.cybellereaper.fusionBlock.weapon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WeaponTest {

    @Test
    void defaultsAreInitialized() {
        Weapon weapon = new Weapon();

        assertNotNull(weapon.getId());
        assertEquals("Weapon", weapon.getName());
        assertEquals(WeaponEffectParticleType.ELECTRIC_SHOT, weapon.getProjectileEffect());
        assertEquals(WeaponType.PROJECTILE_SHOT, weapon.getType());
        assertEquals(WeaponProjectileDistance.MEDIUM, weapon.getProjectileDistance());
    }
}
