package net.eiradir.server.entity

import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

interface EntityDirtyChunkCache {
    fun isDirty(map: EiradirMap, chunkPos: ChunkDimensions): Boolean
    fun clear()
}