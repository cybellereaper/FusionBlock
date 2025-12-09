package com.github.cybellereaper.fusionBlock.weapon

enum class WeaponType {
    MELEE,        // Swords, and anything close combat

    PROJECTILE_SHOT, // Decided to take a different approach from SINGLE_SHOT, AND TRIPLE_SHOT (shatter gun)

    @Deprecated("Changing approach to damages, read: WeaponType enum")
    SINGLE_SHOT,  // Can only attack singular enemy
    @Deprecated("Changing approach to damages, read: WeaponType enum")
    TRIPLE_SHOT, // Can only attack 3 enemies at once
    THROWABLE,  // Grenades, or any throwable object (they're projectiles like the rest)
}