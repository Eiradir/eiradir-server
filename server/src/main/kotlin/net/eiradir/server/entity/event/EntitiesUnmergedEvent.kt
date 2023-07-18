package net.eiradir.server.entity.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

data class EntitiesUnmergedEvent(val map: EiradirMap, val chunkPos: ChunkDimensions, val entities: Collection<Entity>) {
    override fun toString(): String {
        return "EntitiesUnmergedEvent(map=$map, chunkPos=$chunkPos, entities=${entities.size})"
    }
}