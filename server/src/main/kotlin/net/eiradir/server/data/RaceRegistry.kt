package net.eiradir.server.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class RaceRegistry(idResolver: IdResolver): Registry<Race>("races", idResolver) {
    override fun invalid(name: String): Race {
        return Race(name)
    }
}