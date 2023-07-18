package net.eiradir.server.map

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.EntityLocationCache
import net.eiradir.server.map.*
import net.eiradir.server.map.event.MapLoadedEvent
import net.eiradir.server.map.event.MapUnloadedEvent
import net.eiradir.server.map.view.EditableMapView
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.ServerNetworkContext
import java.util.*

class ClientMapView(
    private val mapManager: MapManager,
    private val scopedMapManager: ScopedMapManager,
    private val entityLocationCache: EntityLocationCache,
    private val eventBus: EventBus,
    val client: ServerNetworkContext
) : MapView, EditableMapView {

    override val dimensions: ChunkDimensions get() = mapManager.dimensions

    private val mapReferenceMapper = mapperFor<net.eiradir.server.entity.components.MapReference>()

    val scopedMaps = mutableListOf<EiradirMap>()

    override val loadedMaps: List<EiradirMap>
        get() = scopedMaps

    override var editingMap: String = mapManager.defaultMap

    private val scopedMapsByName = mutableMapOf<String, EiradirMap>()

    override fun getTileAt(pos: Vector3Int): Tile? {
        var result = mapManager.getTileAt(pos)
        for (map in scopedMaps) {
            val mapTile = map.getTileAt(pos) ?: continue
            if (mapTile != Tile.Invalid) {
                result = mapTile
            }
        }
        return result
    }

    override fun getEntitiesAt(position: Vector3Int): Collection<Entity> {
        // TODO this returns entities even if they're overwritten by a scoped map
        val result = mutableListOf<Entity>()
        result.addAll(mapManager.getEntitiesAt(position))
        for (map in scopedMaps) {
            result.addAll(entityLocationCache.getEntitiesAt(map, position))
        }
        return result
    }

    override fun getEntityById(id: UUID): Entity? {
        // TODO this returns entities even if they're overwritten by a scoped map
        val entity = mapManager.getEntityById(id) ?: return null
        val map = mapReferenceMapper[entity]?.map ?: return null
        if (isLoaded(map) || mapManager.isLoaded(map)) {
            return entity
        } else {
            return null
        }
    }

    override fun load(map: EiradirMap): EiradirMap {
        if (scopedMapManager.loadScoped(map) && !scopedMapsByName.containsKey(map.name)) {
            scopedMaps.add(map)
            scopedMapsByName[map.name] = map
            eventBus.post(MapLoadedEvent(this, map))
        }
        return map
    }

    override fun unload(name: String) {
        scopedMapsByName.remove(name)?.let {
            scopedMaps.remove(it)
            eventBus.post(MapUnloadedEvent(this, it))
        }
    }

    override fun isLoaded(map: EiradirMap): Boolean {
        return mapManager.isMergedMap(map) || scopedMapsByName.containsKey(map.name)
    }

    override fun isLoaded(name: String): Boolean {
        return scopedMapsByName.containsKey(name)
    }

    fun isLoadedOrMerged(map: EiradirMap): Boolean {
        return isLoaded(map) || mapManager.isLoaded(map)
    }

    override fun getLoadedMapByName(name: String): EiradirMap? {
        return scopedMapsByName[name] ?: mapManager.getLoadedMapByName(name)
    }

    override fun getActionableMap(position: Vector3Int): EiradirMap? {
        return when (editingMap) {
            MapManager.MERGED -> {
                val entityMap = client.connectionEntity?.let { mapReferenceMapper[it] }?.map
                if (entityMap == null || mapManager.isLoaded(entityMap)) {
                    getActionableMapFromScoped(position) ?: mapManager.getActionableMapFromMerged(position)
                } else {
                    entityMap
                }
            }

            else -> scopedMapsByName[editingMap] ?: mapManager.getLoadedMapByName(editingMap)
        }
    }

    private fun getActionableMapFromScoped(position: Vector3Int): EiradirMap? {
        for (map in scopedMaps.reversed()) {
            val tile = map.getTileAt(position) ?: continue
            if (tile != Tile.Invalid) {
                return map
            }
        }
        return null
    }

    override fun hasChunkAt(chunkPos: ChunkDimensions): Boolean {
        return mapManager.hasChunkAt(chunkPos) || scopedMaps.any { it.getChunkAt(chunkPos) != null }
    }

}