package com.github.cybellereaper.fusionBlock.weapon;

public enum WeaponType {
    MELEE,
    PROJECTILE_SHOT,
    @Deprecated(since = "1.0", forRemoval = false)
    SINGLE_SHOT,
    @Deprecated(since = "1.0", forRemoval = false)
    TRIPLE_SHOT,
    THROWABLE,
}
