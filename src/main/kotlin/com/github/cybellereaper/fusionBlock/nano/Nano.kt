package com.github.cybellereaper.fusionBlock.nano

import java.util.UUID



data class Nano(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "blah blah", // later on implement kiyo for text component and rich context
    var type: NanoEffectType = NanoEffectType.ADAPTIUM, // set this as a default arg
    var desc: String = "blah blah information",
    var stamina: Double = 100.0, // literally explains itself.
    var isSummoned: Boolean = false,
    var powerCooldownCycle: Long = 0L,
    var powerBoosted: Double = 0.0,
    //    var itemAttached: ... will be done at a later date

    //    var powers: ... will be done at a later date
) {

    fun recoverStamina(damage: Double) {
        if (stamina >= 100.0) return
        stamina += damage
    }

    fun damageStamina(damage: Double) {
        if (stamina <= 0.0) {
            isSummoned = false
            return
        }
        stamina -= damage
    }
}
