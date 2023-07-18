package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int
import java.util.*

interface EntityIdCache {
    fun getEntityById(id: UUID): Entity?
}