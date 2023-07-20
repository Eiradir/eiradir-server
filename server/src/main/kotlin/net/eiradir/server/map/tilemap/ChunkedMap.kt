package net.eiradir.server.map.tilemap

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.data.Tile
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries

interface ChunkedMap {
    val chunkSize get() = 16
    val name: String
    val descriptor: ChunkDimensions
    val registries: Registries
    fun getChunkAt(x: Int, y: Int, z: Int): MapChunk? = getChunkAt(descriptor.of(x, y, z))
    fun getChunkAt(position: Vector3): MapChunk? = getChunkAt(descriptor.of(position))
    fun getChunkAt(position: Vector3Int): MapChunk? = getChunkAt(descriptor.of(position))
    fun getChunkAt(chunkPos: ChunkDimensions): MapChunk?
    fun getOrCreateChunkAt(chunkPos: ChunkDimensions): MapChunk
    fun getOrCreateChunkAt(x: Int, y: Int, z: Int): MapChunk = getOrCreateChunkAt(descriptor.of(x, y, z))
    fun getOrCreateChunkAt(position: Vector3): MapChunk = getOrCreateChunkAt(descriptor.of(position))
    fun getOrCreateChunkAt(position: Vector3Int): MapChunk = getOrCreateChunkAt(descriptor.of(position))
    fun setTileAt(position: Vector3Int, tile: Tile?)
    fun getTileAt(position: Vector3Int): Tile?
    fun getLoadedChunks(): Collection<MapChunk>
    fun isEmpty(): Boolean
    fun isChunkLoaded(chunkPos: ChunkDimensions): Boolean
}