package net.eiradir.server.data

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class TileRegistry(idResolver: IdResolver): Registry<Tile>("tiles", idResolver) {
    override fun invalid(name: String): Tile {
        return Tile(name)
    }
}