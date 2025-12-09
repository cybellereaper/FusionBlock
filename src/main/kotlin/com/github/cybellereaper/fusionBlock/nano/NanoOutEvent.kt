package com.github.cybellereaper.fusionBlock.nano

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

class NanoOutEvent : Cancellable {
    private var isCancelled: Boolean = false

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        this.isCancelled = p0
    }
}