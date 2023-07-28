package net.eiradir.server.data.builder

import net.eiradir.server.data.Item
import net.eiradir.server.data.ItemReference
import net.eiradir.server.data.ItemRegistry
import net.eiradir.server.item.EquipmentSlot

class ItemBuilder(val name: String) {
    var isoName: String = name
    var maxStackSize: Int = 250
    var twoHanded: Boolean = false
    var tooltip: String? = null
    var gameSystem: String? = null
    var restItem: ItemReference? = null
    var equipmentSlot: EquipmentSlot = EquipmentSlot.None
    var tags = mutableSetOf<String>()

    fun tag(tag: String) {
        tags.add(tag)
    }

    fun build(): Item {
        return Item(
            name = name,
            isoName = isoName,
            maxStackSize = maxStackSize,
            equipmentSlot = equipmentSlot,
            twoHanded = twoHanded,
            tooltip = tooltip,
            gameSystem = gameSystem,
            restItem = restItem,
            tags = tags
        )
    }
}