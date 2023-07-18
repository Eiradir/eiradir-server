package net.eiradir.server.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class ItemRegistry(idResolver: IdResolver): Registry<Item>("items", idResolver) {
    override fun invalid(name: String): Item {
        return Item(name)
    }
}