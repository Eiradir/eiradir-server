package net.eiradir.server.entity.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int

data class EntityDirectionChangedEvent(val entity: Entity, val map: EiradirMap, val direction: GridDirection, val oldDirection: GridDirection)