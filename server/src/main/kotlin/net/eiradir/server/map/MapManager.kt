package net.eiradir.server.map

import com.badlogic.ashley.core.Entity
import com.google.common.collect.HashMultiset
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.EntityIdCache
import net.eiradir.server.entity.EntityLocationCache
import net.eiradir.server.extensions.logger
import net.eiradir.server.map.event.*
import net.eiradir.server.map.filter.CompositeMapFilter
import net.eiradir.server.map.filter.MapFilter
import net.eiradir.server.map.filter.NoopMapFilter
import net.eiradir.server.map.tilemap.MapChunk
import net.eiradir.server.map.tilemap.MapChunkProvider
import net.eiradir.server.map.tilemap.MemoryChunkedMap
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.registry.Registries
import java.util.*

class MapManager(
    private val registries: Registries,
    private val entityLocationCache: EntityLocationCache,
    private val entityIdCache: EntityIdCache,
    private val eventBus: EventBus
) : EventBusSubscriber, MapChunkProvider, MapView {

    private val log = logger()
    val defaultMap = "player"

    private val mergedChunkMap = MemoryChunkedMap(registries, MERGED, this)

    val mergedMap = EiradirMap(MERGED, mergedChunkMap)
    override val dimensions get() = mergedMap.dimensions

    override val loadedMaps = mutableListOf<EiradirMap>()
    private val loadedMapsByName = mutableMapOf<String, EiradirMap>()
    private val desiredChunks = HashMultiset.create<ChunkDimensions>()
    private val loadedChunkPositions get() = mergedMap.getLoadedChunks().map { it.dimensions }.toSet() + desiredChunks

    override fun load(map: EiradirMap): EiradirMap {
        if (loadedMapsByName.containsKey(map.name)) {
            log.warn("Tried to load map ${map.name} but it is already loaded!")
            return map
        }

        log.info("Loading map ${map.name}.")
        loadedMaps.add(map)
        loadedMapsByName[map.name] = map

        loadedChunkPositions.forEach { chunkPos ->
            val cachedChunk = mergedMap.getChunkAt(chunkPos)
            if (cachedChunk != null) {
                val sourceChunk = map.getChunkAt(chunkPos)
                if (sourceChunk != null) {
                    mergeChunk(sourceChunk, cachedChunk, map.filter ?: NoopMapFilter)
                    eventBus.post(TileChunkUpdatedEvent(mergedMap, chunkPos, cachedChunk.getBackingArray()))
                } else if (map.filter != null) {
                    mergeChunkFilterOnly(cachedChunk, map.filter ?: NoopMapFilter)
                    eventBus.post(TileChunkUpdatedEvent(mergedMap, chunkPos, cachedChunk.getBackingArray()))
                }
            }
        }

        eventBus.post(MapLoadedEvent(this, map))
        return map
    }

    fun getMapFilterAppliedTo(map: EiradirMap): MapFilter? {
        val mapIndex = loadedMaps.indexOf(map)
        if (mapIndex == -1) {
            return null
        }

        val filters = loadedMaps.subList(mapIndex + 1, loadedMaps.size).mapNotNull { it.filter }
        return if (filters.isNotEmpty()) CompositeMapFilter(filters) else null
    }

    private fun getMapFilterAppliedToMergedMap(): MapFilter {
        val filters = loadedMaps.mapNotNull { it.filter }.toList()
        return if (filters.isNotEmpty()) CompositeMapFilter(filters) else NoopMapFilter
    }

    override fun unload(name: String) {
        loadedMapsByName.remove(name)?.let { map ->
            log.info("Unloading map $name...")
            loadedMaps.remove(map)
            val chunksToRecompute = if (map.filter != null) mergedMap.getLoadedChunks() else map.getLoadedChunks()
            chunksToRecompute.forEach { chunk ->
                recomputeChunk(chunk.dimensions)
            }
            eventBus.post(MapUnloadedEvent(this, map))
        } ?: log.warn("Tried to unload map $name but it is not loaded!")
    }

    private fun recomputeChunk(chunkPos: ChunkDimensions) {
        val mergedFilters = getMapFilterAppliedToMergedMap()
        val cachedChunk = mergedMap.getChunkAt(chunkPos)
        if (cachedChunk != null) {
            loadedMaps.forEach { map ->
                map.getChunkAt(chunkPos)?.let { source ->
                    mergeChunk(source, cachedChunk, mergedFilters)
                }
            }
            cachedChunk.markDirty()
            val cachedTileMap = cachedChunk.getBackingArray()
            eventBus.post(TileChunkUpdatedEvent(mergedMap, chunkPos, cachedTileMap))
        }
    }

    private fun recomputePosition(position: Vector3Int) {
        val chunkPos = mergedMap.dimensions.of(position)
        mergedChunkMap.clearPosition(position)
        val mergedChunk = mergedMap.getChunkAt(chunkPos)
        if (mergedChunk != null) {
            val mergedFilters = getMapFilterAppliedToMergedMap()
            loadedMaps.forEach { map ->
                val sourceChunk = map.getChunkAt(chunkPos)
                if (sourceChunk != null) {
                    mergePosition(sourceChunk, mergedChunk, position, mergedFilters)
                } else if (map.filter != null) {
                    val tile = mergedChunk.getTileAt(position) ?: return@forEach
                    val mappedTile = map.filter!!.mapTile(registries, tile)
                    mergedChunk.setTileAt(position, mappedTile)
                }
            }
            mergedChunk.markDirty()
            eventBus.post(TileUpdatedEvent(mergedMap, chunkPos, position, mergedChunk.getTileAt(position)))
        }
    }

    private fun mergePosition(source: MapChunk, cachedChunk: MapChunk, position: Vector3Int, filter: MapFilter) {
        source.getTileAt(position)?.let { tile ->
            val mappedTile = filter.mapTile(registries, tile)
            if (mappedTile != Tile.Invalid) {
                cachedChunk.setTileAt(position, mappedTile)
            }
        }
    }

    private fun mergeChunk(source: MapChunk, target: MapChunk, filter: MapFilter) {
        val sourceTiles = source.getBackingArray()
        val targetTiles = target.getBackingArray()
        for ((index, tileId) in sourceTiles.withIndex()) {
            val tile = registries.tiles.getById(tileId.toInt()) ?: continue
            val mappedTile = filter.mapTile(registries, tile)
            targetTiles[index] = mappedTile?.id(registries)?.toByte() ?: 0
        }
        target.markNeighboursDirty()
        target.markDirty()
    }

    private fun mergeChunkFilterOnly(target: MapChunk, filter: MapFilter) {
        val targetTiles = target.getBackingArray()
        for ((index, tileId) in targetTiles.withIndex()) {
            val tile = registries.tiles.getById(tileId.toInt()) ?: continue
            val mappedTile = filter.mapTile(registries, tile)
            targetTiles[index] = mappedTile?.id(registries)?.toByte() ?: 0
        }
    }

    override fun requestChunkAt(chunkPos: ChunkDimensions): MapChunk? {
        if (loadedMaps.any { it.getChunkAt(chunkPos) != null }) {
            return mergedChunkMap.getOrCreateChunkAt(chunkPos)
        } else {
            return null
        }
    }

    override fun chunkLoaded(chunk: MapChunk) {
        val mergedFilters = getMapFilterAppliedToMergedMap()
        loadedMaps.forEach { map ->
            val loadedChunk = map.getChunkAt(chunk.dimensions)
            if (loadedChunk != null) {
                mergeChunk(loadedChunk, chunk, mergedFilters)
            }

            eventBus.post(ChunkLoadedEvent(this, map, chunk.dimensions))
        }
    }

    @Subscribe
    fun onTileUpdated(event: TileUpdatedEvent) {
        if (loadedMapsByName.containsKey(event.map.name)) {
            recomputePosition(event.position)
        }
    }

    @Subscribe
    fun onChunkUpdated(event: TileChunkUpdatedEvent) {
        if (loadedMapsByName.containsKey(event.map.name)) {
            recomputeChunk(event.chunkPos)
        }
    }

    override fun toString(): String {
        return "MapManager(loadedMaps=$loadedMaps)"
    }

    fun desireChunk(chunkPos: ChunkDimensions) {
        desiredChunks.add(chunkPos)
    }

    fun freeDesiredChunk(chunkPos: ChunkDimensions) {
        desiredChunks.remove(chunkPos)
    }

    override fun getTileAt(pos: Vector3Int): Tile? {
        return mergedMap.getTileAt(pos)
    }

    override fun getEntitiesAt(position: Vector3Int): Collection<Entity> {
        return loadedMaps.flatMap { entityLocationCache.getEntitiesAt(it, position) }
    }

    override fun getEntityById(id: UUID): Entity? {
        return entityIdCache.getEntityById(id)
    }

    override fun isLoaded(map: EiradirMap): Boolean {
        return loadedMapsByName.containsKey(map.name)
    }

    override fun isLoaded(name: String): Boolean {
        return loadedMapsByName.containsKey(name)
    }

    override fun getLoadedMapByName(name: String): EiradirMap? {
        return loadedMapsByName[name]
    }

    fun isMergedMap(map: EiradirMap): Boolean {
        return map == mergedMap
    }

    private fun findDefaultMapIndex(): Int {
        val defaultMapIndex = loadedMaps.indexOfFirst { it.name == defaultMap }
        if (defaultMapIndex == -1) {
            return 0
        }
        return defaultMapIndex
    }

    fun getActionableMapFromMerged(position: Vector3Int): EiradirMap? {
        val defaultMapIndex = findDefaultMapIndex()
        for (i in (defaultMapIndex..loadedMaps.lastIndex).reversed()) {
            val map = loadedMaps[i]
            val tile = map.getTileAt(position) ?: continue
            if (tile != Tile.Invalid) {
                return map
            }
        }
        return null
    }

    override fun hasChunkAt(chunkPos: ChunkDimensions): Boolean {
        return loadedMaps.any { it.getChunkAt(chunkPos) != null }
    }

    fun getDefaultMap(): EiradirMap {
        return loadedMapsByName[defaultMap] ?: throw IllegalStateException("Default map $defaultMap not loaded")
    }

    companion object {
        const val MERGED = "merged"
    }
}