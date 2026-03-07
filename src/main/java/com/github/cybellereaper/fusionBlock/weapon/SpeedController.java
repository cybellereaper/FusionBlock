package com.github.cybellereaper.fusionBlock.weapon;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class SpeedController implements Listener {
    private final JavaPlugin plugin;
    private final WeaponManager gunManager;
    private final Map<UUID, Double> baseSpeed = new HashMap<>();
    private final Map<UUID, Boolean> adsState = new HashMap<>();

    public SpeedController(JavaPlugin plugin, WeaponManager gunManager) {
        this.plugin = plugin;
        this.gunManager = gunManager;

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getOnlinePlayers().forEach(SpeedController.this::applyCurrentSpeed);
            }
        }.runTaskTimer(plugin, 1L, 2L);
    }

    public void setAds(Player player, boolean ads) {
        adsState.put(player.getUniqueId(), ads);
        applyCurrentSpeed(player);
    }

    public boolean isAds(Player player) {
        return adsState.getOrDefault(player.getUniqueId(), false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        rememberBaseSpeed(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        baseSpeed.remove(event.getPlayer().getUniqueId());
        adsState.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> applyCurrentSpeed(event.getPlayer()));
    }

    private void rememberBaseSpeed(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr != null) {
            baseSpeed.putIfAbsent(player.getUniqueId(), attr.getBaseValue());
        }
    }

    public void applyCurrentSpeed(Player player) {
        rememberBaseSpeed(player);

        AttributeInstance attr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr == null) return;

        double base = baseSpeed.getOrDefault(player.getUniqueId(), attr.getBaseValue());
        ItemStack held = player.getInventory().getItemInMainHand();
        Weapon gun = gunManager.fromItem(held);

        if (gun == null) {
            attr.setBaseValue(base);
            return;
        }

        double multiplier = isAds(player) ? gun.adsMoveSpeedMultiplier() : gun.hipMoveSpeedMultiplier();
        attr.setBaseValue(base * multiplier);
    }
}