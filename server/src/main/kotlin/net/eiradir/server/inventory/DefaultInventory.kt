package net.eiradir.server.inventory

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.item.ItemOwner
import java.util.*

// TODO we should really move the json stuff out of the gameplay class considering it's not even the real final persistence method
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class DefaultInventory(override val id: UUID, val size: Int) : Inventory, ItemOwner<Int> {

    @field:JsonProperty("items")
    private val _items: MutableList<ItemInstance> = mutableListOf()

    @get:JsonIgnore
    override val items: List<ItemInstance> get() = _items

    @field:JsonIgnore
    private val _observers: MutableList<InventoryObserver> = mutableListOf()

    @get:JsonIgnore
    override val observers: List<InventoryObserver> get() = _observers

    init {
        ensureSize(size)
    }

    private fun ensureSize(size: Int) {
        if (_items.size < size) {
            _items.addAll(List(size - _items.size) { ItemInstance.Empty })
            observers.forEach { it.sizeChanged(this, size) }
        }
    }

    override fun setItem(slot: Int, itemInstance: ItemInstance) {
        ensureSize(slot + 1)
        _items[slot] = itemInstance
        itemInstance.setOwner(this, slot)
        observers.forEach { it.onSlotChanged(this, slot) }
    }

    override fun getItem(slotId: Int): ItemInstance {
        return _items.getOrNull(slotId) ?: ItemInstance.Empty
    }

    override fun itemInstanceChanged(context: Int, itemInstance: ItemInstance) {
        observers.forEach { it.onSlotChanged(this, context) }
    }

    override fun replaceItemInstance(context: Int, itemInstance: ItemInstance) {
        setItem(context, itemInstance)
    }

    override fun addObserver(observer: InventoryObserver) {
        observers.forEach { it.observerAdded(this) }
        _observers.add(observer)
    }

    override fun removeObserver(observer: InventoryObserver) {
        _observers.remove(observer)
        observers.forEach { it.observerRemoved(this) }
    }
}