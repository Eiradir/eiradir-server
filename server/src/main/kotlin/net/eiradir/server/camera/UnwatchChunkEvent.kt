package net.eiradir.server.camera

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.network.ClientEntityEvent

class UnwatchChunkEvent(entity: Entity, val chunkPos: ChunkDimensions) : ClientEntityEvent(entity) {
    override fun toString(): String {
        return "UnwatchChunkEvent(chunkPos=$chunkPos)"
    }
}