package net.eiradir.server.combat

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class ArmorRegistry(idResolver: IdResolver) : Registry<Armor>("armors", idResolver) {
    override fun invalid(name: String): Armor {
        return Armor(name, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    }
}