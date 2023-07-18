package net.eiradir.server.hud

import net.eiradir.server.chat.ChatMessageType
import net.eiradir.server.hud.property.NoHudProperties
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.writeEnum
import java.util.UUID

enum class ChatHudMessages {
    /**
     * This message is sent to the client when adding a new chat message, and received on the server when the client submits a message.
     */
    SubmitChatMessage,
    ChatMessage,
}

class ChatHud : Hud<NoHudProperties, ChatHudMessages>() {

    override val propertyKeys = NoHudProperties.values()
    override val messageKeys = ChatHudMessages.values()
    override val typeName: String get() = "chat"
    private var submitHandler: (ChatHud, String) -> Unit = {_,_ ->}

    fun sendChatMessage(message: String, type: ChatMessageType, fromEntityId: UUID?) {
        sendMessage(ChatHudMessages.ChatMessage) {
            it.writeString(message)
            it.writeEnum(type)
            it.writeUniqueId(fromEntityId ?: UUID(0, 0))
        }
    }

    fun onMessageSubmitted(handler: (ChatHud, String) -> Unit): ChatHud {
        submitHandler = handler
        return this
    }

    override fun messageReceived(key: ChatHudMessages, buf: SupportedInput) {
        super.messageReceived(key, buf)
        when (key) {
            ChatHudMessages.SubmitChatMessage -> {
                val message = buf.readString()
                submitHandler(this, message)
            }
            else -> {}
        }
    }

}