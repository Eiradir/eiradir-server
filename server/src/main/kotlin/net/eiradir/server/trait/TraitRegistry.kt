package net.eiradir.server.trait

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class TraitRegistry(idResolver: IdResolver): Registry<Trait>("traits", idResolver) {
    override fun invalid(name: String): Trait {
        return Trait(name)
    }
}