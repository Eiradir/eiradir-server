package net.eiradir.content

import com.badlogic.ashley.core.Entity
import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.message.NoHudMessages
import net.eiradir.server.services

enum class SpellsHudProperties {
    Spells
}

class SpellsHud(val entity: Entity) : Hud<SpellsHudProperties, NoHudMessages>() {
    override val typeName = "spells"
    override val propertyKeys = SpellsHudProperties.values()
    override val messageKeys = NoHudMessages.values()

    private val spells = createIconArrayProperty(SpellsHudProperties.Spells)
        .from { entity.services().stats.queryStatsByTag(entity, "spell", { _, value -> value > 0}).keys.toTypedArray() }
        .throttled(1f)
}