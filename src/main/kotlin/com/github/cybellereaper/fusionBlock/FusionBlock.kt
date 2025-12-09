package com.github.cybellereaper.fusionBlock

import com.github.cybellereaper.fusionBlock.nano.TestNanoFunc
import org.bukkit.plugin.java.JavaPlugin

class FusionBlock : JavaPlugin() {
    override fun onEnable() {




        TestNanoFunc(this)
//        DashMechanic(this)
    }

    override fun onDisable() {
        TestNanoFunc.removeAllNanos()
    }
}
