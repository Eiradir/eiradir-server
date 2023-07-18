package net.eiradir.server.entity.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.map.EiradirMap

data class EntitySwitchedMapEvent(val entity: Entity, val oldMap: EiradirMap, val newMap: EiradirMap)