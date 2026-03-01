package com.github.cybellereaper.fusionBlock.nano;

public enum NanoEffectType {
    COSMIX,
    BLASTONS,
    ADAPTIUM;

    public void applyDamageInteraction(NanoEffectType attacker, Nano receiver) {
        if (attacker == null || receiver == null) {
            return;
        }

        double multiplier = attacker.getDamageMultiplierAgainst(receiver.getType());
        if (multiplier > 1.0) {
            receiver.recoverStamina(1.5);
        }
    }

    public boolean hasAdvantageOver(NanoEffectType other) {
        return (this == ADAPTIUM && other == BLASTONS)
                || (this == BLASTONS && other == COSMIX)
                || (this == COSMIX && other == ADAPTIUM);
    }

    public double getDamageMultiplierAgainst(NanoEffectType defender) {
        if (defender == null) {
            throw new IllegalArgumentException("defender");
        }
        if (this == defender) {
            return 1.0;
        }
        if (hasAdvantageOver(defender)) {
            return 1.25;
        }
        return 0.75;
    }

    public AdvantageIndicator getAdvantageIndicatorAgainst(NanoEffectType defender) {
        if (defender == null) {
            throw new IllegalArgumentException("defender");
        }
        if (this == defender) {
            return AdvantageIndicator.EQUAL;
        }
        return hasAdvantageOver(defender) ? AdvantageIndicator.ADVANTAGE : AdvantageIndicator.DISADVANTAGE;
    }

    public enum AdvantageIndicator {
        ADVANTAGE,
        EQUAL,
        DISADVANTAGE
    }
}
