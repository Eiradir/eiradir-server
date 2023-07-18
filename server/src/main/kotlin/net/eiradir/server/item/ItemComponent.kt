package net.eiradir.server.item

import com.badlogic.ashley.core.Component
import net.eiradir.server.entity.components.PersistedComponent
import net.eiradir.server.extensions.CloneableComponent
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class ItemComponent : PersistedComponent, CloneableComponent {

    var itemInstance: ItemInstance = ItemInstance.Empty

    override val serializedName = "Item"

    override fun save(buf: SupportedOutput) {
        if (itemInstance.isEmpty) {
            buf.writeShort(0)
            return
        } else {
            buf.writeShort(itemInstance.count)
            buf.writeId(itemInstance.item)
        }
    }

    override fun load(buf: SupportedInput) {
        val count = buf.readShort().toInt()
        if (count == 0) {
            itemInstance = ItemInstance.Empty
            return
        }
        val item = buf.readFromRegistry { it.items }
        itemInstance = ItemInstance(item, count)
    }

    override fun copy(): Component {
        return ItemComponent().also {
            it.itemInstance = itemInstance
        }
    }
}