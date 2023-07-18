package net.eiradir.server.map.view

import com.badlogic.ashley.core.Entity
import net.eiradir.server.data.Tile
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int
import java.util.*

interface MapView {
    val dimensions: ChunkDimensions
    val loadedMaps: List<EiradirMap>
    fun getTileAt(pos: Vector3Int): Tile?
    fun load(map: EiradirMap): EiradirMap
    fun unload(name: String)
    fun isLoaded(map: EiradirMap): Boolean
    fun isLoaded(name: String): Boolean
    fun getEntitiesAt(position: Vector3Int): Collection<Entity>
    fun getEntityById(id: UUID): Entity?
    fun getLoadedMapByName(name: String): EiradirMap?
    fun hasChunkAt(chunkPos: ChunkDimensions): Boolean
}