package net.eiradir.server.chat

import com.badlogic.ashley.core.Entity
import net.eiradir.server.network.NetworkContext

data class ChatEvent(val client: NetworkContext, val entity: Entity, val type: ChatMessageType, val message: String)