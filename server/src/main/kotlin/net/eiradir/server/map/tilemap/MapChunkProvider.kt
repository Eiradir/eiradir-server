package net.eiradir.server.map.tilemap

import net.eiradir.server.map.ChunkDimensions

interface MapChunkProvider {
    fun requestChunkAt(chunkPos: ChunkDimensions): MapChunk?
    fun chunkLoaded(chunk: MapChunk)
}