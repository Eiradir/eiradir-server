package net.eiradir.server.camera.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.network.event.ClientEntityEvent

class WatchChunkEvent(entity: Entity, val chunkPos: ChunkDimensions) : ClientEntityEvent(entity) {
    override fun toString(): String {
        return "WatchChunkEvent(chunkPos=$chunkPos)"
    }
}