package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int

interface EntityLocationCache {
    fun getEntitiesIn(map: EiradirMap, chunkPos: ChunkDimensions): Collection<Entity>
    fun getEntitiesAt(map: EiradirMap, pos: Vector3Int): Collection<Entity>
    fun getEntitiesInRange(map: EiradirMap, pos: Vector3Int, range: Int): Collection<Entity>

    fun entityChangedPosition(entity: Entity, oldPos: Vector3Int, newPos: Vector3Int)
}