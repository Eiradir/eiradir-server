package net.eiradir.server.map.tilemap

import net.eiradir.server.data.Tile
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries

class MemoryChunkedMap(override val registries: Registries, override val name: String, private var provider: MapChunkProvider? = null) : ChunkedMap {

    override val descriptor = ChunkDimensions(0, 0, 0, chunkSize)

    private val chunks = mutableMapOf<ChunkDimensions, MapChunk>()

    override fun getLoadedChunks(): Collection<MapChunk> {
        return chunks.values
    }

    override fun getChunkAt(chunkPos: ChunkDimensions): MapChunk? {
        return chunks[chunkPos] ?: provider?.requestChunkAt(chunkPos)
    }

    override fun getOrCreateChunkAt(chunkPos: ChunkDimensions): MapChunk {
        return chunks[chunkPos] ?: return MapChunk(this, chunkPos).also {
            chunks[chunkPos] = it
        }.also {
            provider?.chunkLoaded(it)
        }
    }

    override fun setTileAt(position: Vector3Int, tile: Tile?) {
        val chunkPos = descriptor.of(position)
        val chunk = if (tile != null) getOrCreateChunkAt(chunkPos) else getChunkAt(chunkPos)
        chunk?.setTileAt(position, tile)
    }

    override fun getTileAt(position: Vector3Int): Tile? {
        return getChunkAt(descriptor.of(position))?.getTileAt(position)
    }

    fun clear() {
        chunks.keys.forEach { clearChunk(it) }
    }

    fun clearChunk(chunkPos: ChunkDimensions) {
        val tileMap = chunks[chunkPos]?.getBackingArray() ?: return
        tileMap.fill(0)
    }

    fun clearPosition(position: Vector3Int) {
        chunks[descriptor.of(position)]?.setTileAt(position, null)
    }

    override fun isChunkLoaded(chunkPos: ChunkDimensions): Boolean {
        return chunks.containsKey(chunkPos)
    }

    override fun toString(): String {
        return "MapCache(name='$name', descriptor=$descriptor, chunks=${chunks.size})"
    }
}