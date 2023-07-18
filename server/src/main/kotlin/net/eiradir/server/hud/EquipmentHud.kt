package net.eiradir.server.hud

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.InventoryComponent
import net.eiradir.server.hud.message.NoHudMessages

enum class EquipmentHudProperties {
    Inventory
}

class EquipmentHud(private val target: Entity) : Hud<EquipmentHudProperties, NoHudMessages>() {
    override val propertyKeys = EquipmentHudProperties.values()
    override val messageKeys = NoHudMessages.values()
    override val typeName: String get() = "equipment"
    private val inventoryMapper = mapperFor<InventoryComponent>()
    private val inventory = createInventoryProperty(EquipmentHudProperties.Inventory).from { inventoryMapper[target]?.defaultInventory }
}