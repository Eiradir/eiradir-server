package net.eiradir.server.persistence

import net.eiradir.server.inventory.Inventory
import java.util.*

interface InventoryStorage {
    fun createInventory(size: Int): Inventory
    fun loadInventoryById(inventoryId: UUID): Inventory?
    fun save(inventory: Inventory)
}