package net.eiradir.server.player.event

import com.badlogic.ashley.core.Entity

data class PlayerLeftEvent(val connection: Entity, val entity: Entity)