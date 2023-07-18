package net.eiradir.content

import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.message.NoHudMessages
import net.eiradir.server.hud.property.NoHudProperties

class MinimapHud : Hud<NoHudProperties, NoHudMessages>() {
    override val typeName = "minimap"
    override val propertyKeys = NoHudProperties.values()
    override val messageKeys = NoHudMessages.values()
}