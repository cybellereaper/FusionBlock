package com.github.cybellereaper.fusionBlock.weapon;

import org.bukkit.Material;

public record Weapon(
        String id,
        String displayName,
        Material material,
        double damage,
        double headshotMultiplier,
        double range,
        double spreadHipDegrees,
        double spreadAdsDegrees,
        int rpm,
        int magazineSize,
        long reloadMillis,
        double hipMoveSpeedMultiplier,
        double adsMoveSpeedMultiplier
) {
    public long shotDelayMillis() {
        return Math.max(1L, Math.round(60000.0 / rpm));
    }
}
