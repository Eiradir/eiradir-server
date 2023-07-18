package net.eiradir.server.map.filter

import com.badlogic.ashley.core.Entity
import net.eiradir.server.data.Tile
import net.eiradir.server.registry.Registries


data class CompositeMapFilter(private val filters: List<MapFilter>) : MapFilter {
    override fun mapTile(registries: Registries, tile: Tile): Tile? {
        var result = tile
        for (filter in filters) {
            result = filter.mapTile(registries, result) ?: return null
        }
        return result
    }

    override fun mapEntity(registries: Registries, entity: Entity): Entity? {
        var result = entity
        filters.forEach { filter ->
            result = filter.mapEntity(registries, result) ?: return null
        }
        return result
    }
}