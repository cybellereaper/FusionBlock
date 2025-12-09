package com.github.cybellereaper.fusionBlock.nano

import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.cos
import kotlin.math.sin

class TestNanoFunc(private val javaPlugin: JavaPlugin) : Listener {
    init {
        Bukkit.getServer().pluginManager.registerEvents(this, javaPlugin)
    }

    companion object {
        private val testingNanoManager: HashMap<Player, ArmorStand> = java.util.HashMap()

        fun removeAllNanos() {
            testingNanoManager.values.forEach { it.remove() }
            testingNanoManager.clear()
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val armorStand = event.player.world.spawn(event.player.location, ArmorStand::class.java) {
            it.setGravity(false)
        }
        
        try {
            val blueprint = ModelEngineAPI.getBlueprint("numbuh_three")

            val createActiveModel = ModelEngineAPI.createActiveModel(blueprint)
            val modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(armorStand)
            modeledEntity.addModel(createActiveModel, true)

            modeledEntity.isBaseEntityVisible = false
            createActiveModel.setScale(0.4)
            createActiveModel.modeledEntity = modeledEntity
            createActiveModel.setAutoRendererInitialization(true)
            createActiveModel.generateModel()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        testingNanoManager.putIfAbsent(event.player, armorStand)
        startAnimation(event.player)
    }

    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        val armorStand = testingNanoManager[event.player] ?: return
        armorStand.remove()
        testingNanoManager.remove(event.player)
    }

    private fun startAnimation(player: Player) {
        val nanoManager = testingNanoManager[player] ?: return

        object : BukkitRunnable() {
            private var time = 0f
            private val haloRadius = 1.5
            private val bobbingHeight = 0.0
            private val bobbingSpeed = 0
            private val rotationSpeed = 2
            private val pauseDuration = 4000

            override fun run() {
                time += 1f
                val location = calculateLocation(player.location.clone())
                nanoManager.teleport(location) // No need to reassign to manager
            }

            private fun calculateLocation(location: Location): Location {
                val haloAngle = time % 360.0
                val (xOffset, zOffset) = calculateOffset(haloRadius, haloAngle)
                location.add(xOffset, 0.0, zOffset)

                val yOffset = bobbingHeight * sin(time * bobbingSpeed) + 1.4
                location.add(0.0, yOffset, 0.0)

                val rotationAngle = if ((time % (pauseDuration * 2)) < pauseDuration)
                    (time * rotationSpeed) % 360f else (time - pauseDuration) * rotationSpeed % 360f

                location.yaw = rotationAngle

                return location
            }

            private fun calculateOffset(radius: Double, angle: Double): Pair<Double, Double> {
                val radianAngle = Math.toRadians(angle)
                return Pair(radius * cos(radianAngle), radius * sin(radianAngle))
            }

        }.runTaskTimer(javaPlugin, 0L, 1L)
    }
}