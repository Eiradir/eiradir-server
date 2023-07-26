package net.eiradir.server.playercontroller

import com.badlogic.ashley.core.Entity

data class PlayerControlGainedEvent(val connection: Entity, val entity: Entity)