package net.eiradir.server.culling

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

data class EntitiesUnculledEvent(val map: EiradirMap, val chunkPos: ChunkDimensions, val entities: Collection<Entity>) {
    override fun toString(): String {
        return "EntitiesUnculledEvent(map=$map, chunkPos=$chunkPos, entities=${entities.size})"
    }
}