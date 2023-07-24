package net.eiradir.server.commands

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType

class CommandSourceArgument(private val serverCommandSource: ServerCommandSource) : ArgumentType<CommandSourceProvider> {

    override fun <S> parse(reader: StringReader): CommandSourceProvider {
        when (val username = reader.readUnquotedString()) {
            "server" -> return CommandSourceProvider.Server(serverCommandSource)
            "-c" -> return CommandSourceProvider.ByCursorPosition
            "-s" -> return CommandSourceProvider.BySelection
            else -> return CommandSourceProvider.ByUsername(username)
        }
    }

    companion object {
        private val ERROR_UNKNOWN_SOURCE = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown source $value") }

        fun source(serverCommandSource: ServerCommandSource): CommandSourceArgument {
            return CommandSourceArgument(serverCommandSource)
        }

        fun getSource(context: CommandContext<CommandSource>, name: String): CommandSource {
            val provider = context.getArgument(name, CommandSourceProvider::class.java)
            return provider.get(context) ?: throw ERROR_UNKNOWN_SOURCE.create(provider.toString(context))
        }
    }
}