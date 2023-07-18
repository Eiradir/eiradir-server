package net.eiradir.server.map.filter

import com.badlogic.ashley.core.Entity
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.eiradir.server.data.Tile
import net.eiradir.server.registry.Registries

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = SimpleMapFilter::class, name = "simple"),
    JsonSubTypes.Type(value = CompositeMapFilter::class, name = "composite")
)
interface MapFilter {
    fun mapTile(registries: Registries, tile: Tile): Tile?
    fun mapEntity(registries: Registries, entity: Entity): Entity?
}