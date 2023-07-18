package net.eiradir.server.entity.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int

data class EntityPositionChangedEvent(val entity: Entity, val map: EiradirMap, val position: Vector3Int, val oldPosition: Vector3Int)