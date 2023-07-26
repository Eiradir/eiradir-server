package net.eiradir.server.playercontroller

import com.badlogic.ashley.core.Entity

data class PlayerControlReleasedEvent(val connection: Entity, val entity: Entity)