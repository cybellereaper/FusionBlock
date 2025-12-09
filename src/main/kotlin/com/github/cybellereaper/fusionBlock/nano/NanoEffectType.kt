package com.github.cybellereaper.fusionBlock.nano

import com.ticxo.modelengine.api.ModelEngineAPI

enum class NanoEffectType {
    COSMIX, BLASTONS, ADAPTIUM;



    fun shouldDoDamageCompare(damageTypes: Pair<NanoEffectType, Nano>) {
        val (attacker, receiver) = damageTypes

        if (attacker == receiver.type) return // do nothing, because both same type

        if (attacker == COSMIX && receiver.type == BLASTONS ) {
            receiver.recoverStamina(1.5)
        }
    }
}