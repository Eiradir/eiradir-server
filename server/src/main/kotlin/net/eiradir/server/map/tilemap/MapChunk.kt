package net.eiradir.server.map.tilemap

import net.eiradir.server.data.Tile
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int

class MapChunk(val map: ChunkedMap, val dimensions: ChunkDimensions) {

    private val tileMap = ByteArray(dimensions.size * dimensions.size)
    var isDirty = false; private set

    fun replaceBackingArray(tilemap: ByteArray): ByteArray {
        System.arraycopy(tilemap, 0, this.tileMap, 0, tilemap.size)
        return this.tileMap
    }

    fun getBackingArray(): ByteArray {
        return tileMap
    }

    fun setTileAt(position: Vector3Int, tile: Tile?) {
        val relX = dimensions.toRelativeX(position.x)
        val relZ = dimensions.toRelativeY(position.y)

        val index = relZ * dimensions.size + relX
        if (tileMap.indices.contains(index)) {
            tileMap[index] = tile?.id(map.registries)?.toByte() ?: 0
        }

        markDirty()

        // Only mark neighbours as dirty if a tile on the chunk border changed
        if (relX == 0 || relZ == 0 || relX == dimensions.size - 1 || relZ == dimensions.size - 1) {
            markNeighboursDirty()
        }
    }

    fun getTileAt(position: Vector3Int): Tile? {
        val relY = dimensions.toRelativeLevel(position.level)
        if (relY != 0) {
            return null
        }

        val relX = dimensions.toRelativeX(position.x)
        val relZ = dimensions.toRelativeY(position.y)
        return tileMap.getOrNull(relZ * dimensions.size + relX)?.let { if (it > 0) map.registries.tiles.getById(it.toInt()) else null }
    }

    fun hasTileAt(position: Vector3Int): Boolean {
        return getTileAt(position) != null
    }

    fun markNeighboursDirty() {
        for (dir in GridDirection.horizontalValues) {
            val chunkPos = dimensions.offset(dir)
            if (map.isChunkLoaded(chunkPos)) {
                map.getChunkAt(chunkPos)?.markDirty()
            }
        }
    }

    fun markDirty() {
        isDirty = true
    }

    fun resetDirty() {
        isDirty = false
    }

    override fun toString(): String {
        return "MapChunk(descriptor=$dimensions)"
    }

}