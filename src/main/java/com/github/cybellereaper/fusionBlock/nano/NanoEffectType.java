package com.github.cybellereaper.fusionBlock.nano;

/**
 * FusionFall nano matter types with rock/paper/scissors interactions.
 */
public enum NanoEffectType {
    COSMIX,
    BLASTONS,
    ADAPTIUM;

    /**
     * Legacy behavior retained for compatibility with older tests.
     */
    public void applyDamageInteraction(NanoEffectType attacker, Nano receiver) {
        if (attacker == null || receiver == null) {
            return;
        }

        if (attacker == receiver.getType()) {
            return;
        }

        if (attacker == COSMIX && receiver.getType() == BLASTONS) {
            receiver.recoverStamina(1.5);
        }
    }

    /**
     * True when this type has advantage over the other type.
     */
    public boolean hasAdvantageOver(NanoEffectType other) {
        if (other == null) {
            return false;
        }

        return (this == ADAPTIUM && other == BLASTONS)
                || (this == BLASTONS && other == COSMIX)
                || (this == COSMIX && other == ADAPTIUM);
    }

    /**
     * Computes UI indicator for damage bonus/penalty for an attack.
     */
    public CombatIndicator indicatorAgainst(NanoEffectType defender) {
        if (defender == null || this == defender) {
            return CombatIndicator.EQUAL;
        }

        if (hasAdvantageOver(defender)) {
            return CombatIndicator.ADVANTAGE;
        }

        return CombatIndicator.DISADVANTAGE;
    }
}
