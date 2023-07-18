package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import net.eiradir.server.entity.network.NetworkedDataKey
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class ColorComponent(var color: Color = Color.WHITE) : Component, PersistedComponent, NetworkedComponent {
    override val serializedName = "Color"

    override fun save(buf: SupportedOutput) {
        buf.writeColor(color)
    }

    override fun load(buf: SupportedInput) {
        color = buf.readColor()
    }

}