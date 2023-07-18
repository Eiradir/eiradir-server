package net.eiradir.server.inventory

import net.eiradir.server.item.ItemInstance
import java.util.UUID

interface Inventory {
    fun setItem(slot: Int, itemInstance: ItemInstance)
    fun getItem(slotId: Int): ItemInstance
    fun addObserver(observer: InventoryObserver)
    fun removeObserver(observer: InventoryObserver)

    val id: UUID
    val items: List<ItemInstance>
    val observers: List<InventoryObserver>
}