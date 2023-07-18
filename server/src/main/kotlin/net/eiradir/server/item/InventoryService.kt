package net.eiradir.server.item

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.data.Item
import net.eiradir.server.entity.components.InventoryComponent
import net.eiradir.server.inventory.Inventory
import net.eiradir.server.persistence.InventoryStorage
import net.eiradir.server.player.InventoryKeys
import net.eiradir.server.services

class InventoryService(private val inventoryStorage: InventoryStorage) {
    private val inventoryMapper = mapperFor<InventoryComponent>()

    fun getDefaultInventory(entity: Entity, inventoryComponent: InventoryComponent? = inventoryMapper[entity]): Inventory {
        val component = inventoryComponent ?: InventoryComponent().also(entity::add)
        val inventory = component.defaultInventory
        if (inventory == null) {
            val newInventory = inventoryStorage.createInventory(18)
            component.defaultInventory = newInventory
            component.inventoryIds[InventoryKeys.DEFAULT] = newInventory.id
            return newInventory
        }
        return inventory
    }

    fun findSuitableEquipmentSlot(inventory: Inventory, item: Item): Int {
        for (slotId in item.equipmentSlot.slotIds) {
            if (inventory.getItem(slotId).isEmpty) {
                return slotId
            }
        }
        return -1
    }

    fun canMerge(from: ItemInstance, into: ItemInstance): Boolean {
        return from.item == into.item
    }

    fun merge(from: ItemInstance, into: ItemInstance): ItemInstance {
        if (canMerge(from, into)) {
            val spaceLeft = into.item.maxStackSize - into.count
            val toMerge = spaceLeft.coerceAtMost(from.count)
            into.grow(toMerge)
            from.shrink(toMerge)
            return from
        } else {
            into.replaceWith(from)
            return into
        }
    }

    fun merge(from: ItemInstance, inventory: Inventory, slot: Int): ItemInstance {
        val slotItem = inventory.getItem(slot)
        if (slotItem.isEmpty) {
            inventory.setItem(slot, from)
            return ItemInstance.Empty
        } else {
            return merge(from, slotItem)
        }
    }

    fun giveOrDropItem(entity: Entity, itemInstance: ItemInstance) {
        val rest = addItemToInventory(getDefaultInventory(entity), itemInstance)
        if (rest.isNotEmpty) {
            val entityService = entity.services().entities
            entityService.createEntity(rest, entityService.getEntityPosition(entity)).tap { itemEntity ->
                entityService.spawnEntity(itemEntity)
            }
        }
    }

    fun addItemToInventory(inventory: Inventory, itemInstance: ItemInstance): ItemInstance {
        var rest = itemInstance
        var firstEmptySlot = -1
        for ((i, item) in inventory.items.withIndex()) {
            if (item.isEmpty && firstEmptySlot == -1) {
                firstEmptySlot = i
            }
            if (item.isNotEmpty && canMerge(rest, item)) {
                rest = merge(rest, inventory, i)
                if (rest.isEmpty) {
                    return ItemInstance.Empty
                }
            }
        }
        if (rest.isNotEmpty && firstEmptySlot != -1) {
            inventory.setItem(firstEmptySlot, rest)
            return ItemInstance.Empty
        }
        return rest
    }
}