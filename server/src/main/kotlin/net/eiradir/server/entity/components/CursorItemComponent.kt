package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.inventory.DefaultInventory
import net.eiradir.server.inventory.Inventory
import java.util.*

class CursorItemComponent : Component {
    val inventory: Inventory = DefaultInventory(UUID.randomUUID(), 1)
}