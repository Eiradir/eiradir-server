package net.eiradir.server.hud

import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class HudType(override val name: String) : RegistryEntry<HudType> {
    override fun registry(registries: Registries): Registry<HudType> {
        return registries.hudTypes
    }
}