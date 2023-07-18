package net.eiradir.content

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.CursorItemComponent
import net.eiradir.server.hud.EquipmentHudProperties
import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.message.NoHudMessages

enum class CursorHudProperties {
    CursorItem
}

class CursorHud(connection: Entity) : Hud<CursorHudProperties, NoHudMessages>() {
    override val propertyKeys = CursorHudProperties.values()
    override val messageKeys = NoHudMessages.values()
    override val typeName: String get() = "cursor"

    private val cursorItemMapper = mapperFor<CursorItemComponent>()

    private val inventory = createInventoryProperty(CursorHudProperties.CursorItem).from { cursorItemMapper[connection]?.inventory }
}


