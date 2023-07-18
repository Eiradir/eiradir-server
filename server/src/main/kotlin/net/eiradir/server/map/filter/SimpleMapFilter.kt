package net.eiradir.server.map.filter

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.data.Tile
import net.eiradir.server.extensions.copy
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.registry.Registries


class SimpleMapFilter : MapFilter {
    val mappedTiles = mutableMapOf<String, String>()
    val removedTiles = mutableSetOf<String>()
    val mappedFixtures = mutableMapOf<String, String>()
    val removedFixtures = mutableSetOf<String>()

    private val itemMapper = mapperFor<ItemComponent>()

    override fun mapTile(registries: Registries, tile: Tile): Tile? {
        if (removedTiles.contains(tile.name)) {
            return null
        }

        val mappedTileName = mappedTiles[tile.name]
        if (mappedTileName != null) {
            return registries.tiles.getByName(mappedTileName)
        }

        return tile
    }

    override fun mapEntity(registries: Registries, entity: Entity): Entity? {
        val item = itemMapper[entity]
        if (item != null) {
            if (removedFixtures.contains(item.itemInstance.item.name)) {
                return null
            }
            val mappedItemName = mappedFixtures[item.itemInstance.item.name]
            if (mappedItemName != null) {
                val mappedItem = registries.items.getByName(mappedItemName) ?: return null
                val mappedEntity = entity.copy()
                itemMapper[mappedEntity].itemInstance = ItemInstance(mappedItem, item.itemInstance.count)
                return mappedEntity
            }
        }
        return entity
    }
}