package net.eiradir.server.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class StatTypeRegistry(idResolver: IdResolver): Registry<StatType>("stats", idResolver) {
    override fun invalid(name: String): StatType {
        return StatType(name)
    }
}