package net.eiradir.server.data

import com.badlogic.ashley.core.Component
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import kotlin.reflect.KClass

data class ComponentRegistryEntry(override val name: String, private val clazz: KClass<out Component>) : RegistryEntry<net.eiradir.server.data.ComponentRegistryEntry> {
    fun create(): Component {
        return clazz.java.getConstructor().newInstance()
    }

    override fun registry(registries: Registries): Registry<net.eiradir.server.data.ComponentRegistryEntry> {
        return registries.components
    }
}