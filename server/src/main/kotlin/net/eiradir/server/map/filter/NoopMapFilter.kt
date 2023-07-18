package net.eiradir.server.map.filter

import com.badlogic.ashley.core.Entity
import net.eiradir.server.data.Tile
import net.eiradir.server.registry.Registries


object NoopMapFilter : MapFilter {
    override fun mapTile(registries: Registries, tile: Tile): Tile? {
        return tile
    }

    override fun mapEntity(registries: Registries, entity: Entity): Entity? {
        return entity
    }
}