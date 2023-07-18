package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.player.VisualTraitInstanceData

class VisualTraitsComponent : Component, PersistedComponent, NetworkedComponent {
    override val serializedName = "VisualTraits"

    val visualTraits = mutableListOf<VisualTraitInstanceData>()

    override fun save(buf: SupportedOutput) {
        buf.writeByte(visualTraits.size)
        visualTraits.forEach {
            buf.writeShort(it.id)
            buf.writeColor(Color(it.color))
        }
    }

    override fun load(buf: SupportedInput) {
        val count = buf.readByte()
        for (i in 0 until count) {
            val visualTraitId = buf.readShort().toInt()
            val color = buf.readColor()
            visualTraits.add(VisualTraitInstanceData(visualTraitId, color.toIntBits()))
        }
    }

}