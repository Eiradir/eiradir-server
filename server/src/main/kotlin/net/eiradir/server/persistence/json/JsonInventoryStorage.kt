package net.eiradir.server.persistence.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.eiradir.server.data.Item
import net.eiradir.server.inventory.DefaultInventory
import net.eiradir.server.inventory.Inventory
import net.eiradir.server.inventory.InventoryObserver
import net.eiradir.server.inventory.WeakObserver
import net.eiradir.server.persistence.InventoryStorage
import net.eiradir.server.registry.Registries
import java.io.File
import java.util.*

class JsonInventoryStorage(private val registries: Registries) : InventoryStorage, InventoryObserver, WeakObserver {
    private val baseDir = File("jdata")
    private val module = SimpleModule().apply {
        addSerializer(JsonItemSerializer())
        addDeserializer(Item::class.java, JsonItemDeserializer(registries))
    }
    private val objectMapper = ObjectMapper().registerKotlinModule().registerModule(module)
    private val inventories = mutableMapOf<UUID, Inventory>()

    override fun createInventory(size: Int): Inventory {
        val inventory = DefaultInventory(UUID.randomUUID(), size)
        inventory.addObserver(this)
        inventories[inventory.id] = inventory
        return inventory
    }

    override fun loadInventoryById(inventoryId: UUID): Inventory? {
        val existing = inventories[inventoryId]
        if (existing != null) {
            return existing
        }
        val inventoryFile = File(baseDir, "inventories/$inventoryId.json")
        if (inventoryFile.exists()) {
            val inventory = objectMapper.readValue<DefaultInventory>(inventoryFile)
            for ((slot, itemInstance) in inventory.items.withIndex()) {
                itemInstance.setOwner(inventory, slot)
            }
            inventory.addObserver(this)
            inventories[inventory.id] = inventory
            return inventory
        }
        return null
    }

    override fun save(inventory: Inventory) {
        inventories[inventory.id] = inventory
        val inventoryFile = File(baseDir, "inventories/${inventory.id}.json")
        inventoryFile.parentFile.mkdirs()
        objectMapper.writeValue(inventoryFile, inventory)
        if (inventory.observers.none { it !is WeakObserver }) {
            release(inventory)
        }
    }

    override fun observerRemoved(inventory: Inventory) {
        if (inventory.observers.any { it !is WeakObserver }) {
            save(inventory)
        }
    }

    private fun release(inventory: Inventory) {
        inventories.remove(inventory.id)
    }
}
