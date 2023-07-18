package net.eiradir.server.hud

import net.eiradir.server.inventory.Inventory
import net.eiradir.server.inventory.InventoryObserver

class HudInventory<TProperty : Enum<TProperty>>(val hud: Hud<TProperty, *>, val key: TProperty) : InventoryObserver {
    var inventory: Inventory? = null; private set
    private var provider: (() -> Inventory?)? = null

    fun from(provider: () -> Inventory?): HudInventory<TProperty> {
        this.provider = provider
        return this
    }

    fun update() {
        if (inventory == null) {
            inventory = provider?.invoke()
            inventory?.let {
                it.addObserver(this)
                sendFullInventory()
            }
        }
    }

    fun cleanup() {
        inventory?.removeObserver(this)
    }

    override fun onSlotChanged(inventory: Inventory, slot: Int) {
        sendSlotUpdate(slot)
    }

    override fun sizeChanged(inventory: Inventory, size: Int) {
        sendFullInventory()
    }

    fun sendFullInventory() {
        inventory?.let { hud.sendInventory(key, it) }
    }

    fun sendSlotUpdate(slotId: Int) {
        val itemStack = inventory?.getItem(slotId) ?: return
        hud.sendInventorySlot(key, slotId, itemStack)
    }
}