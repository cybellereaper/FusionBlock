package com.github.cybellereaper.fusionBlock.weapon;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class WeaponListener implements Listener {
    private final JavaPlugin plugin;
    private final WeaponManager gunManager;
    private final SpeedController speedController;
    private final Map<UUID, WeaponState> states = new HashMap<>();

    public WeaponListener(JavaPlugin plugin, WeaponManager gunManager, SpeedController speedController) {
        this.plugin = plugin;
        this.gunManager = gunManager;
        this.speedController = speedController;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Weapon gun = gunManager.fromItem(player.getInventory().getItemInMainHand());
        if (gun == null) return;

        event.setCancelled(true);
        tryFire(player, gun);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Weapon gun = gunManager.fromItem(player.getInventory().getItemInMainHand());
        if (gun == null) return;

        event.setCancelled(true);
        startReload(player, gun);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Weapon gun = gunManager.fromItem(player.getInventory().getItemInMainHand());
        if (gun == null) return;

        WeaponState state = state(player, gun);
        state.ads = event.isSneaking();
        speedController.setAds(player, event.isSneaking());
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ItemStack held = player.getInventory().getItemInMainHand();
            Weapon gun = gunManager.fromItem(held);
            speedController.setAds(player, false);

            if (gun != null) {
                state(player, gun);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        states.remove(event.getPlayer().getUniqueId());
    }

    private WeaponState state(Player player, Weapon gun) {
        return states.computeIfAbsent(player.getUniqueId(), id -> new WeaponState(gun.magazineSize()));
    }

    private void tryFire(Player shooter, Weapon gun) {
        WeaponState state = state(shooter, gun);
        long now = System.currentTimeMillis();

        if (state.reloading) {
            if (now >= state.reloadFinishAt) {
                finishReload(shooter, gun, state);
            } else {
                return;
            }
        }

        if (now - state.lastShotAt < gun.shotDelayMillis()) return;

        if (state.ammoInMag <= 0) {
            startReload(shooter, gun);
            return;
        }

        state.lastShotAt = now;
        state.ammoInMag--;

        fireHitscan(shooter, gun, state.ads);

        if (state.ammoInMag <= 0) {
            startReload(shooter, gun);
        }
    }

    private void startReload(Player player, Weapon gun) {
        WeaponState state = state(player, gun);
        if (state.reloading) return;
        if (state.ammoInMag >= gun.magazineSize()) return;

        state.reloading = true;
        state.reloadFinishAt = System.currentTimeMillis() + gun.reloadMillis();

        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 0.8f, 1.4f);
        player.sendMessage("Reloading...");
    }

    private void finishReload(Player player, Weapon gun, WeaponState state) {
        state.reloading = false;
        state.ammoInMag = gun.magazineSize();
        player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 0.9f, 1.2f);

        player.sendMessage("Reloaded");
    }

    private void fireHitscan(Player shooter, Weapon gun, boolean ads) {
        Location eye = shooter.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        double spread = ads ? gun.spreadAdsDegrees() : gun.spreadHipDegrees();
        dir = applySpread(dir, spread);

        World world = shooter.getWorld();

        RayTraceResult blockHit = world.rayTraceBlocks(
                eye,
                dir,
                gun.range(),
                FluidCollisionMode.NEVER,
                true
        );

        double maxDistance = gun.range();
        if (blockHit != null && blockHit.getHitPosition() != null) {
            maxDistance = eye.toVector().distance(blockHit.getHitPosition());
        }

        RayTraceResult entityHit = world.rayTraceEntities(
                eye,
                dir,
                maxDistance,
                1.5,
                entity -> entity instanceof LivingEntity && entity != shooter
        );

        Location end = eye.clone().add(dir.clone().multiply(maxDistance));
        world.spawnParticle(Particle.CRIT, eye.clone().add(dir.clone().multiply(0.6)), 2, 0, 0, 0, 0);
        drawTracer(world, eye, end);

        shooter.playSound(shooter.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.55f, ads ? 1.6f : 1.3f);

        if (entityHit == null || !(entityHit.getHitEntity() instanceof LivingEntity target)) {
            if (blockHit != null && blockHit.getHitPosition() != null) {
                Location hit = blockHit.getHitPosition().toLocation(world);
                world.spawnParticle(Particle.SMOKE, hit, 6, 0.05, 0.05, 0.05, 0.0);
                world.playSound(hit, Sound.BLOCK_STONE_HIT, 0.6f, 1.7f);
            }
            return;
        }

        double damage = computeDamage(gun, eye, target);
        target.damage(damage, shooter);

        if (entityHit.getHitPosition() != null) {
            Location hit = entityHit.getHitPosition().toLocation(world);
            world.spawnParticle(Particle.DAMAGE_INDICATOR, hit, 6, 0.1, 0.1, 0.1, 0.0);
            world.playSound(hit, Sound.ENTITY_ARROW_HIT_PLAYER, 0.8f, 1.2f);
        }
    }

    private double computeDamage(Weapon gun, Location from, LivingEntity target) {
        double distance = from.distance(target.getEyeLocation());
        double falloff = Math.max(0.65, 1.0 - ((distance / gun.range()) * 0.35));

        double damage = gun.damage() * falloff;

        double headY = target.getEyeLocation().getY();
        double centerY = target.getLocation().getY() + (target.getHeight() * 0.6);

        if (Math.abs(headY - centerY) > 0.2) {
            damage *= gun.headshotMultiplier();
        }

        return damage;
    }

    private Vector applySpread(Vector dir, double spreadDegrees) {
        double radians = Math.toRadians(spreadDegrees);
        double yawOffset = ThreadLocalRandom.current().nextGaussian() * radians;
        double pitchOffset = ThreadLocalRandom.current().nextGaussian() * radians;

        Vector right = dir.clone().crossProduct(new Vector(0, 1, 0));
        if (right.lengthSquared() < 1.0E-6) {
            right = new Vector(1, 0, 0);
        } else {
            right.normalize();
        }

        Vector up = right.clone().crossProduct(dir).normalize();

        return dir.clone()
                .add(right.multiply(Math.tan(yawOffset)))
                .add(up.multiply(Math.tan(pitchOffset)))
                .normalize();
    }

    private void drawTracer(World world, Location start, Location end) {
        Vector delta = end.toVector().subtract(start.toVector());
        double distance = delta.length();
        if (distance <= 0.001) return;

        Vector step = delta.normalize().multiply(0.5);
        Location current = start.clone();
        for (double d = 0; d < distance; d += 0.5) {
            world.spawnParticle(Particle.END_ROD, current, 1, 0, 0, 0, 0);
            current.add(step);
        }
    }
}