package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.entity.network.NetworkedDataKey
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

class NameComponent(var name: String = "") : Component, PersistedComponent, NetworkedComponent {
    override val serializedName = "Name"

    override fun save(buf: SupportedOutput) {
        buf.writeString(name)
    }

    override fun load(buf: SupportedInput) {
        name = buf.readString()
    }

}