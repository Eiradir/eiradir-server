package net.eiradir.content

import com.badlogic.ashley.core.Entity
import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.message.NoHudMessages
import net.eiradir.server.trait.TraitsService

enum class TraitsHudProperties {
    Traits
}

class TraitsHud(private val traitsService: TraitsService, private val entity: Entity) : Hud<TraitsHudProperties, NoHudMessages>() {
    override val typeName = "traits"
    override val propertyKeys = TraitsHudProperties.values()
    override val messageKeys = NoHudMessages.values()

    //private val traits = createListProperty(TraitsHudProperties.Traits).from { traitsService.getTraits(entity) }
}