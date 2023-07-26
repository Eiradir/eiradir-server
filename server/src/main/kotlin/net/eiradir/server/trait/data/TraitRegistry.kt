package net.eiradir.server.trait.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry
import net.eiradir.server.trait.data.Trait

class TraitRegistry(idResolver: IdResolver): Registry<Trait>("traits", idResolver) {
    override fun invalid(name: String): Trait {
        return Trait(name)
    }
}