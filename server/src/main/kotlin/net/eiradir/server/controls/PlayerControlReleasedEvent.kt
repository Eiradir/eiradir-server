package net.eiradir.server.controls

import com.badlogic.ashley.core.Entity

data class PlayerControlReleasedEvent(val connection: Entity, val entity: Entity)