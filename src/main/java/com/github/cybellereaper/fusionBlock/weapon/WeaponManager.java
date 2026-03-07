package com.github.cybellereaper.fusionBlock.weapon;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaponManager {

    private final NamespacedKey gunIdKey;
    private final Map<String, Weapon> guns = new LinkedHashMap<>();

     public WeaponManager(JavaPlugin plugin) {
        this.gunIdKey = new NamespacedKey(plugin, "gun_id");
    }

        public void registerDefaults() {
        register(new Weapon(
                "pistol", "Pistol", Material.IRON_HORSE_ARMOR,
                6.0, 1.7, 60.0,
                2.8, 0.7,
                400, 12, 1400,
                0.92, 0.72
        ));

        register(new Weapon(
                "rifle", "Rifle", Material.BLAZE_ROD,
                10.0, 1.5, 160.0,
                2.0, 0.45,
                320, 30, 1,
                0.88, 0.68
        ));

        register(new Weapon(
                "smg", "SMG", Material.CARROT_ON_A_STICK,
                4.0, 1.4, 45.0,
                3.4, 1.0,
                900, 35, 1600,
                0.95, 0.78
        ));

        register(new Weapon(
                "sniper", "Sniper", Material.SPYGLASS,
                16.0, 2.0, 140.0,
                1.8, 0.08,
                55, 5, 2400,
                0.82, 0.50
        ));
    }

     public void register(Weapon gun) {
        guns.put(gun.id().toLowerCase(Locale.ROOT), gun);
    }

    public Collection<Weapon> all() {
        return guns.values();
    }

    public Weapon get(String id) {
        return guns.get(id.toLowerCase(Locale.ROOT));
    }

    public NamespacedKey gunIdKey() {
        return gunIdKey;
    }

    public ItemStack createGunItem(Weapon gun) {
        ItemStack item = new ItemStack(gun.material(), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + gun.displayName());
        meta.getPersistentDataContainer().set(gunIdKey, PersistentDataType.STRING, gun.id());
        meta.setRarity(ItemRarity.UNCOMMON);
        item.setItemMeta(meta);
        return item;
    }

    public Weapon fromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(gunIdKey, PersistentDataType.STRING);
        if (id == null) return null;
        return get(id);
    }
}
