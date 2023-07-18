package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

interface PersistedComponent : Component {
    val serializedName: String
    fun save(buf: SupportedOutput)
    fun load(buf: SupportedInput)
}