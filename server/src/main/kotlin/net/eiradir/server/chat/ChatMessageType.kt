package net.eiradir.server.chat

enum class ChatMessageType(private val format: String, val colorKey: String, val rangeMultiplier: Float) {
    Normal("%s: %s", "chat_normal", 1f),
    Action("%s%s", "chat_action", 1f),
    Shout("%s: %s", "chat_shout", 2f),
    Whisper("%s: %s", "chat_whisper", 0.1f),
    Inform("%s", "chat_inform", 0f),
    CombatSplash("%s", "chat_combat", 1f),
    Debug("%s", "chat_inform", 1f),
    Error("%s", "chat_error", 0f);

    fun format(sender: String, message: String): String {
        if (this == Inform) {
            return message
        }
        return format.format(sender, message)
    }

    companion object {
        private val values = values()
        fun fromId(id: Int): ChatMessageType {
            return values[id]
        }
    }
}

