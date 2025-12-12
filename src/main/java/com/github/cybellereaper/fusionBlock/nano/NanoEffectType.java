package com.github.cybellereaper.fusionBlock.nano;

public enum NanoEffectType {
    COSMIX,
    BLASTONS,
    ADAPTIUM;

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
}
