package net.eiradir.server.culling

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions

interface EntityCullingResolver {
    fun shouldBeCulled(entity: Entity, chunkPos: ChunkDimensions): Boolean
}