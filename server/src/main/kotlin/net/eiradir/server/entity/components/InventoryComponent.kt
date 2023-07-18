package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.inventory.Inventory
import java.util.*

class InventoryComponent(var inventoryIds: MutableMap<String, UUID> = mutableMapOf()) : Component, PersistedComponent, NetworkedComponent {
    override val serializedName = "Inventory"

    val inventories = mutableMapOf<String, Inventory>()
    var defaultInventory: Inventory? = null

    override fun save(buf: SupportedOutput) {
        buf.writeByte(inventoryIds.size)
        inventoryIds.forEach { (key, value) ->
            buf.writeString(key)
            buf.writeUniqueId(value)
        }
    }

    override fun load(buf: SupportedInput) {
        val size = buf.readByte()
        val map = mutableMapOf<String, UUID>()
        repeat(size.toInt()) {
            val key = buf.readString()
            val value = buf.readUniqueId()
            map[key] = value
        }
        inventoryIds = map
    }

}