package com.github.cybellereaper.fusionBlock.weapon

import java.util.UUID

data class Weapon(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "Weapon",

    @Deprecated("Changing approach to damages, read: WeaponType enum")
    var damage: Pair<Double, Double> = Pair(0.0, 0.0), // Single enemy, or triple enemy damage
    var projectileEffect: WeaponEffectParticleType = WeaponEffectParticleType.ELECTRIC_SHOT,
    var type: WeaponType = WeaponType.PROJECTILE_SHOT,
    var projectileDistance: WeaponProjectileDistance = WeaponProjectileDistance.MEDIUM
)
