package net.eiradir.server.map

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.EngineQueue
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.map.filter.MapFilter
import net.eiradir.server.map.tilemap.ChunkedMap
import net.eiradir.server.map.tilemap.MapChunk
import net.eiradir.server.map.tilemap.PersistentChunkedMap
import net.eiradir.server.math.Vector3Int

data class EiradirMap(val name: String, val chunkedMap: ChunkedMap) {

    private val idMapper = mapperFor<IdComponent>()

    var engineQueue: EngineQueue? = null; private set
    var filter: MapFilter? = null; private set
    var removalList: EntityRemovalList? = null; private set

    val dimensions: ChunkDimensions = chunkedMap.descriptor

    fun withEntityRemovalList(removalList: EntityRemovalList?): EiradirMap {
        this.removalList = removalList
        return this
    }

    fun withFilter(filter: MapFilter?): EiradirMap {
        this.filter = filter
        return this
    }

    fun reflectToEngine(engineQueue: EngineQueue): EiradirMap {
        this.engineQueue = engineQueue
        return this
    }

    fun getChunkAt(chunkPos: ChunkDimensions): MapChunk? {
        return chunkedMap.getChunkAt(chunkPos)
    }

    fun getChunkAt(position: Vector3Int): MapChunk? {
        return chunkedMap.getChunkAt(position)
    }

    fun getOrCreateChunkAt(chunkPos: ChunkDimensions): MapChunk {
        return chunkedMap.getOrCreateChunkAt(chunkPos)
    }

    fun getTileAt(pos: Vector3Int): Tile? {
        return chunkedMap.getTileAt(pos)
    }

    fun setTileAt(position: Vector3Int, tile: Tile?) {
        chunkedMap.setTileAt(position, tile)
    }

    fun getLoadedChunks(): Collection<MapChunk> {
        return chunkedMap.getLoadedChunks()
    }

    override fun toString(): String {
        return "EiradirMap(name='$name')"
    }

    fun removeEntityNonDestructively(entity: Entity) {
        val removalList = removalList ?: EntityRemovalList().also { removalList = it }
        removalList.removeEntity(entity)
    }

    fun restoreEntity(entity: Entity) {
        removalList?.restoreEntity(entity)
    }

    fun isEntityRemoved(entity: Entity): Boolean {
        val id = idMapper[entity]?.id ?: return false
        return removalList?.isEntityRemoved(id) ?: false
    }

    fun saveAllChanged() {
        (chunkedMap as? PersistentChunkedMap)?.saveAllChanged()
    }
}