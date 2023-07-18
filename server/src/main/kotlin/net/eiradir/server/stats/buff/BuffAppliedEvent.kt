package net.eiradir.server.stats.buff

import com.badlogic.ashley.core.Entity

data class BuffAppliedEvent(val entity: Entity, val buffInstance: BuffInstance)
