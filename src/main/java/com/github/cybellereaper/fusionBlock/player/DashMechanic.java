package com.github.cybellereaper.fusionBlock.player;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;

public class DashMechanic implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long dashCooldown = 5_000L;
    private final LongSupplier timeSource;

    public DashMechanic(JavaPlugin javaPlugin) {
        this(javaPlugin, Clock.systemUTC()::millis);
    }

    public DashMechanic(JavaPlugin javaPlugin, LongSupplier timeSource) {
        this.timeSource = timeSource;
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void onDoubleTapW(PlayerToggleSprintEvent event) {
        if (event.isSprinting()) {
            initiateDash(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().isFlying()) {
            initiateDash(event.getPlayer());
        }
    }

    private void initiateDash(Player player) {
        long currentTime = timeSource.getAsLong();
        long lastDashTime = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (currentTime - lastDashTime < dashCooldown) {
            long secondsLeft = ((lastDashTime + dashCooldown) - currentTime) / 1000;
            player.sendMessage("You cannot dash for another " + secondsLeft + " seconds.");
            return;
        }

        Vector direction = player.getLocation().getDirection().normalize().multiply(2);
        player.setVelocity(player.getVelocity().add(direction));

        var particleLocation = player.getLocation().clone();
        for (int i = 0; i <= 9; i++) {
            if (particleLocation.getWorld() != null) {
                particleLocation.getWorld().spawnParticle(Particle.CLOUD, particleLocation, 1, 0.0, 0.0, 0.0, 0.0);
            }
            particleLocation.add(direction.multiply(0.1));
        }

        cooldowns.put(player.getUniqueId(), currentTime);
    }
}
