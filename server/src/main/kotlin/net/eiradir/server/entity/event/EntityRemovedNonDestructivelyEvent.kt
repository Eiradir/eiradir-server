package net.eiradir.server.entity.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

data class EntityRemovedNonDestructivelyEvent(val map: EiradirMap, val chunkPos: ChunkDimensions, val entity: Entity)