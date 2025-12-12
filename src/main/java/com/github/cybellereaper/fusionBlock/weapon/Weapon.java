package com.github.cybellereaper.fusionBlock.weapon;

import java.util.Objects;
import java.util.UUID;

public class Weapon {
    private String id = UUID.randomUUID().toString();
    private String name = "Weapon";
    @Deprecated(forRemoval = false, since = "1.0")
    private WeaponDamage damage = new WeaponDamage(0.0, 0.0);
    private WeaponEffectParticleType projectileEffect = WeaponEffectParticleType.ELECTRIC_SHOT;
    private WeaponType type = WeaponType.PROJECTILE_SHOT;
    private WeaponProjectileDistance projectileDistance = WeaponProjectileDistance.MEDIUM;

    public Weapon() {
    }

    public Weapon(String id, String name, WeaponDamage damage, WeaponEffectParticleType projectileEffect,
                  WeaponType type, WeaponProjectileDistance projectileDistance) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.damage = Objects.requireNonNull(damage, "damage");
        this.projectileEffect = Objects.requireNonNull(projectileEffect, "projectileEffect");
        this.type = Objects.requireNonNull(type, "type");
        this.projectileDistance = Objects.requireNonNull(projectileDistance, "projectileDistance");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public WeaponDamage getDamage() {
        return damage;
    }

    public void setDamage(WeaponDamage damage) {
        this.damage = Objects.requireNonNull(damage, "damage");
    }

    public WeaponEffectParticleType getProjectileEffect() {
        return projectileEffect;
    }

    public void setProjectileEffect(WeaponEffectParticleType projectileEffect) {
        this.projectileEffect = Objects.requireNonNull(projectileEffect, "projectileEffect");
    }

    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public WeaponProjectileDistance getProjectileDistance() {
        return projectileDistance;
    }

    public void setProjectileDistance(WeaponProjectileDistance projectileDistance) {
        this.projectileDistance = Objects.requireNonNull(projectileDistance, "projectileDistance");
    }
}
