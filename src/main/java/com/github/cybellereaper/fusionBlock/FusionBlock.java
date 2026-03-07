package com.github.cybellereaper.fusionBlock;

import com.github.cybellereaper.fusionBlock.nano.TestNanoFunc;
import com.github.cybellereaper.fusionBlock.weapon.GunCommand;
import com.github.cybellereaper.fusionBlock.weapon.SpeedController;
import com.github.cybellereaper.fusionBlock.weapon.WeaponListener;
import com.github.cybellereaper.fusionBlock.weapon.WeaponManager;

import org.bukkit.plugin.java.JavaPlugin;

public final class FusionBlock extends JavaPlugin {

    @Override
    public void onEnable() {
        var weaponManager = new WeaponManager(this);
        var speedController = new SpeedController(this, weaponManager);
        weaponManager.registerDefaults();
        // new TestNanoFunc(this);
        // new DashMechanic(this);
        getCommand("gun").setExecutor(new GunCommand(weaponManager));
        this.getServer().getPluginManager().registerEvents(new WeaponListener(this, weaponManager, speedController),
                this);
        this.getServer().getPluginManager().registerEvents(speedController, this);
    }

    @Override
    public void onDisable() {
        TestNanoFunc.removeAllNanos();
    }
}
