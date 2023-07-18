package net.eiradir.server.player.login

import com.badlogic.ashley.core.Entity
import net.eiradir.server.player.GameCharacter

data class PlayerJoinedEvent(val connection: Entity, val entity: Entity, val character: GameCharacter)