package net.eiradir.server.chat

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.plugin.Initializer

class ChatCommands(dispatcher: CommandDispatcher<CommandSource>, chatService: ChatService) : Initializer {
    init {
    }
}