package net.eiradir.server.inventory

interface InventoryObserver {
    fun onSlotChanged(inventory: Inventory, slot: Int) = Unit
    fun sizeChanged(inventory: Inventory, size: Int) = Unit
    fun observerAdded(inventory: Inventory) = Unit
    fun observerRemoved(inventory: Inventory) = Unit
}