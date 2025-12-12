package com.github.cybellereaper.fusionBlock;

import com.github.cybellereaper.fusionBlock.nano.TestNanoFunc;
import org.bukkit.plugin.java.JavaPlugin;

public final class FusionBlock extends JavaPlugin {

    @Override
    public void onEnable() {
        new TestNanoFunc(this);
        // new DashMechanic(this);
    }

    @Override
    public void onDisable() {
        TestNanoFunc.removeAllNanos();
    }
}
