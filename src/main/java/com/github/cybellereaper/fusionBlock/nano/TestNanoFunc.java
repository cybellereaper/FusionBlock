package com.github.cybellereaper.fusionBlock.nano;

import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TestNanoFunc implements Listener {
    private final JavaPlugin javaPlugin;

    private static final Map<Player, ArmorStand> TESTING_NANO_MANAGER = new HashMap<>();

    // I want to track what slot they hit

    private static final Map<Player, Integer> TESTING_PLAYER_SLOT = new HashMap<>();

    public TestNanoFunc(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    public static void removeAllNanos() {
        TESTING_NANO_MANAGER.values().forEach(ArmorStand::remove);
        TESTING_NANO_MANAGER.clear();
    }

    @EventHandler
    public void testOutMethod(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
       
        int mainHandSlot = player.getInventory().getHeldItemSlot();
        int offHandSlot = 40; // Off-hand is always slot 40
        int newSlot = (mainHandSlot == offHandSlot) ? 0 : offHandSlot; // Toggle between main hand and off-hand
        TESTING_PLAYER_SLOT.put(player, newSlot);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();

        ArmorStand armorStand = world.spawn(location, ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.setInvulnerable(true);
        });

        // try {
        //     var blueprint = ModelEngineAPI.getBlueprint("numbuh_three");
        //     var createActiveModel = ModelEngineAPI.createActiveModel(blueprint);
        //     var modeledEntity = ModelEngineAPI.getOrCreateModeledEntity(armorStand);
        //     modeledEntity.addModel(createActiveModel, true);

        //     modeledEntity.setBaseEntityVisible(false);
        //     createActiveModel.setScale(0.4);
        //     createActiveModel.setModeledEntity(modeledEntity);
        //     createActiveModel.setAutoRendererInitialization(true);
        //     createActiveModel.generateModel();
        // } catch (Throwable e) {
        //     e.printStackTrace();
        // }

        TESTING_NANO_MANAGER.putIfAbsent(player, armorStand);
        startAnimation(player);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        ArmorStand armorStand = TESTING_NANO_MANAGER.get(event.getPlayer());
        if (armorStand == null) {
            return;
        }
        armorStand.remove();
        TESTING_NANO_MANAGER.remove(event.getPlayer());
    }

    private void startAnimation(Player player) {
        ArmorStand nanoManager = TESTING_NANO_MANAGER.get(player);
        if (nanoManager == null) {
            return;
        }

        new BukkitRunnable() {
            private float time = 0f;
            private final double haloRadius = 1.5;
            private final double bobbingHeight = 0.0;
            private final double bobbingSpeed = 0;
            private final double rotationSpeed = 2;
            private final long pauseDuration = 4000;

            @Override
            public void run() {
                time += 1f;
                Location location = calculateLocation(player.getLocation().clone());
                nanoManager.teleport(location);
            }

            private Location calculateLocation(Location location) {
                double haloAngle = time % 360.0;
                var offset = calculateOffset(haloRadius, haloAngle);
                location.add(offset.getKey(), 0.0, offset.getValue());

                double yOffset = bobbingHeight * Math.sin(time * bobbingSpeed) + 1.4;
                location.add(0.0, yOffset, 0.0);

                float rotationAngle;
                if ((time % (pauseDuration * 2)) < pauseDuration) {
                    rotationAngle = (float) ((time * rotationSpeed) % 360f);
                } else {
                    rotationAngle = (float) (((time - pauseDuration) * rotationSpeed) % 360f);
                }

                location.setYaw(rotationAngle);
                return location;
            }

            private Map.Entry<Double, Double> calculateOffset(double radius, double angle) {
                double radianAngle = Math.toRadians(angle);
                return Map.entry(radius * Math.cos(radianAngle), radius * Math.sin(radianAngle));
            }

        }.runTaskTimer(javaPlugin, 0L, 1L);
    }
}
