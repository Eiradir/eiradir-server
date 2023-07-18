package net.eiradir.server.data

import com.badlogic.ashley.core.Component
import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class ComponentRegistry(idResolver: IdResolver): Registry<net.eiradir.server.data.ComponentRegistryEntry>("components", idResolver) {
    override fun invalid(name: String): net.eiradir.server.data.ComponentRegistryEntry {
        return net.eiradir.server.data.ComponentRegistryEntry(name, Component::class)
    }
}