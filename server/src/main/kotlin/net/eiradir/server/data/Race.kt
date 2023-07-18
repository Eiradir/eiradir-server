package net.eiradir.server.data

import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Race(override val name: String, val isoName: String = name) : RegistryEntry<Race>, IsoType {

    override fun registry(registries: Registries): Registry<Race> {
        return registries.races
    }

    companion object {
        val Invalid = Race("invalid")
    }

    override fun isoId(registries: Registries): Int {
        return registries.idResolver.resolve("isos", isoName) ?: 0
    }

}