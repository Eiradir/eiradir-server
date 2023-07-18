package net.eiradir.server.interact

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class InteractionRegistry(idResolver: IdResolver): Registry<Interaction>("interactions", idResolver) {
    override fun invalid(name: String): Interaction {
        return Interaction(name)
    }
}