package net.eiradir.server.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.InventoryComponent
import net.eiradir.server.inventory.InventoryObserver
import net.eiradir.server.persistence.InventoryStorage
import net.eiradir.server.player.InventoryKeys

class InventorySystem(private val inventoryStorage: InventoryStorage) : EntitySystem(), EntityListener, InventoryObserver {

    private val inventoryMapper = mapperFor<InventoryComponent>()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun entityAdded(entity: Entity) {
        val inventoryComponent = inventoryMapper[entity] ?: return
        inventoryComponent.inventoryIds.forEach {
            val inventory = inventoryStorage.loadInventoryById(it.value) ?: return@forEach
            inventoryComponent.inventories[it.key] = inventory
            inventory.addObserver(this)
        }
        inventoryComponent.defaultInventory = inventoryComponent.inventories[InventoryKeys.DEFAULT]
    }

    override fun entityRemoved(entity: Entity) {
        val inventoryComponent = inventoryMapper[entity] ?: return
        inventoryComponent.inventories.forEach { (_, inventory) ->
            inventory.removeObserver(this)
        }
    }

    companion object {
        private val family = allOf(InventoryComponent::class).get()
    }
}