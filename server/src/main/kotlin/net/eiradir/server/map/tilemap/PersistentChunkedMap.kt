package net.eiradir.server.map.tilemap

import net.eiradir.server.data.Tile
import net.eiradir.server.io.SupportedDataInputStream
import net.eiradir.server.io.SupportedDataOutputStream
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.registry.Registries
import java.io.*

class PersistentChunkedMap(override val registries: Registries, mapsDirectory: File, override val name: String) : ChunkedMap {

    private val chunks = mutableMapOf<ChunkDimensions, MapChunk>()
    private val mapDirectory = File(mapsDirectory, name)

    override val descriptor = ChunkDimensions(0, 0, 0, chunkSize)

    init {
        if (!mapDirectory.exists()) {
            if (!mapDirectory.mkdirs()) {
                throw RuntimeException("Could not create directory for tile map storage.")
            }
        }
    }

    override fun getLoadedChunks(): Collection<MapChunk> {
        return chunks.values
    }

    override fun getChunkAt(chunkPos: ChunkDimensions): MapChunk? {
        return loadChunkAt(chunkPos)
    }

    override fun getOrCreateChunkAt(chunkPos: ChunkDimensions): MapChunk {
        return loadChunkAt(chunkPos) ?: return MapChunk(this, chunkPos).also {
            chunks[chunkPos] = it
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

    fun saveAllChanged() {
        for (chunk in chunks.values) {
            if (chunk.isDirty) {
                saveChunkTo(chunk, getChunkFile(chunk.dimensions))
                chunk.resetDirty()
            }
        }
    }

    fun markDirty(chunkPos: ChunkDimensions) {
        chunks[chunkPos]?.markDirty()
    }

    fun loadChunkAt(chunkPos: ChunkDimensions): MapChunk? {
        if (chunks.containsKey(chunkPos)) {
            return chunks[chunkPos]
        }
        return loadChunkFrom(getChunkFile(chunkPos))?.also {
            chunks[chunkPos] = it
        }
    }

    private fun saveChunkTo(chunk: MapChunk, file: File) {
        SupportedDataOutputStream(DataOutputStream(FileOutputStream(file)), registries).use {
            it.writeInt(1)
            it.writeChunkDimensions(chunk.dimensions)
            it.write(chunk.getBackingArray())
        }
    }

    private fun loadChunkFrom(file: File): MapChunk? {
        if (!file.exists()) {
            return null
        }

        SupportedDataInputStream(DataInputStream(FileInputStream(file)), registries).use {
            val version = it.readInt()
            val dimensions = it.readChunkDimensions()
            val chunk = MapChunk(this, dimensions)
            val tileMap = ByteArray(dimensions.size * dimensions.size)
            it.read(tileMap)
            chunk.replaceBackingArray(tileMap)
            return chunk
        }
    }

    private fun getChunkFile(chunkPos: ChunkDimensions): File {
        return File(mapDirectory, "${chunkPos.x}_${chunkPos.y}_${chunkPos.level}.chunk")
    }

    fun loadAllChunks() {
        val files = mapDirectory.listFiles { file -> file.name.endsWith(".chunk") }
        if (files != null) {
            for (file in files) {
                loadChunkFrom(file)?.let {
                    chunks[it.dimensions] = it
                }
            }
        }
    }

    override fun isChunkLoaded(chunkPos: ChunkDimensions): Boolean {
        return chunks.containsKey(chunkPos)
    }

    override fun toString(): String {
        return "MapStorage(name='$name', chunks=${chunks.size})"
    }
}