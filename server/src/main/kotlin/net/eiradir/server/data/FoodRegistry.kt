package net.eiradir.server.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class FoodRegistry(idResolver: IdResolver) : Registry<Food>("foods", idResolver) {
    override fun invalid(name: String): Food {
        return Food(name, 0, 0, null, 0)
    }
}