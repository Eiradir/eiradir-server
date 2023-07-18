package net.eiradir.server.chat

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.services

class ChatService {
    private val transform = mapperFor<GridTransform>()

    fun clientSay(connection: Entity, message: String) {
        val controlledEntity = connection.services().playerController.getControlledEntity(connection)
        if (controlledEntity != null) {
            say(controlledEntity, message)
        } else {
            val position = connection.services().camera.getCameraPosition(connection)
            emit(connection, position, message)
        }
    }

    fun say(entity: Entity, rawMessage: String) {
        val position = transform[entity].position
        emit(entity, position, rawMessage)
    }

    fun emit(connection: Entity, position: Vector3Int, rawMessage: String) {
        val (type, message) = determineMessageType(rawMessage)
        val audionces = connection.services().audionce.getAudionces(position)
        for (audionce in audionces) {
            audionce.chatHandler(connection, message, type)
        }
    }

    private fun determineMessageType(message: String): Pair<ChatMessageType, String> {
        if (message.trim().startsWith("#me")) {
            return ChatMessageType.Action to message.substring(3)
        } else if (message.trim().startsWith("#s ")) {
            return ChatMessageType.Shout to message.substring(3)
        } else if (message.trim().startsWith("#w ")) {
            return ChatMessageType.Whisper to message.substring(3)
        }
        return ChatMessageType.Normal to message
    }
}