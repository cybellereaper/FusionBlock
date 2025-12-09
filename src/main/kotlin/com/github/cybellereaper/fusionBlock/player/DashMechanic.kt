package com.github.cybellereaper.fusionBlock.player

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class DashMechanic(javaPlugin: JavaPlugin) : Listener {
    private val cooldowns = mutableMapOf<UUID, Long>()
    private val dashCooldown = 5 * 1000L

    init {
        Bukkit.getServer().pluginManager.registerEvents(this, javaPlugin)
    }

    @EventHandler
    fun onDoubleTapW(event: PlayerToggleSprintEvent) {
        if (event.isSprinting) {
            initiateDash(event.player)
        }
    }

    // this doesn't detect jumping, idk how to implement yet
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player.isFlying) {
            initiateDash(event.player)
        }
    }

    private fun initiateDash(player: Player) {
        val currentTime = System.currentTimeMillis()
        val lastDashTime = cooldowns[player.uniqueId] ?: 0L // Get last dash time, default to 0

        if (currentTime - lastDashTime < dashCooldown) {
            val secondsLeft = ((lastDashTime + dashCooldown) - currentTime) / 1000
            player.sendMessage("You cannot dash for another $secondsLeft seconds.")
            return
        }

        // Perform dash
        val direction = player.location.direction.normalize().multiply(2)
        player.velocity = player.velocity.add(direction)

        // Spawn particles
        val particleLocation = player.location.clone() // Clone location to avoid modifying original
        for (i in 0..9) {
            particleLocation.world?.spawnParticle(Particle.CLOUD, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
            particleLocation.add(direction.multiply(0.1))
        }

        cooldowns[player.uniqueId] = currentTime // Update cooldown
    }
}