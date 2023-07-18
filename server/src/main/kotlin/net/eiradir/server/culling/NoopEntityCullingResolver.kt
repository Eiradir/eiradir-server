package net.eiradir.server.culling

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions

class NoopEntityCullingResolver : EntityCullingResolver {
    override fun shouldBeCulled(entity: Entity, chunkPos: ChunkDimensions): Boolean {
        return false
    }
}