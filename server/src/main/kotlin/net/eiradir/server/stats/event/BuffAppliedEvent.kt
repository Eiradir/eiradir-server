package net.eiradir.server.stats.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.stats.BuffInstance

data class BuffAppliedEvent(val entity: Entity, val buffInstance: BuffInstance)
