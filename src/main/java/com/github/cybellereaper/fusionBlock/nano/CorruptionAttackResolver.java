package com.github.cybellereaper.fusionBlock.nano;

import java.util.Objects;

/**
 * Applies stamina impact from corruption attacks based on type interactions.
 */
public class CorruptionAttackResolver {

    public void apply(Nano nano, NanoEffectType attackerType, double baseImpact) {
        Objects.requireNonNull(nano, "nano");
        Objects.requireNonNull(attackerType, "attackerType");
        if (baseImpact <= 0.0) {
            return;
        }

        NanoEffectType defenderType = nano.getType();
        if (defenderType.hasAdvantageOver(attackerType)) {
            nano.recoverStamina(baseImpact);
            return;
        }

        if (attackerType.hasAdvantageOver(defenderType)) {
            nano.damageStamina(baseImpact);
            return;
        }

        nano.damageStamina(baseImpact / 2.0);
    }
}
