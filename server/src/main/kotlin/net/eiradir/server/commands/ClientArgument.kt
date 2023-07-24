package net.eiradir.server.commands

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import net.eiradir.server.network.NetworkServer
import net.eiradir.server.network.ServerNetworkContext

class ClientArgument(private val networkServer: NetworkServer) : ArgumentType<ServerNetworkContext> {

    override fun <S> parse(reader: StringReader): ServerNetworkContext {
        val username = reader.readUnquotedString()
        val client = networkServer.clients.asSequence().map { it as ServerNetworkContext }.firstOrNull { it.session?.username == username }
        return client ?: throw ERROR_UNKNOWN_CLIENT.create(username)
    }

    companion object {
        private val ERROR_UNKNOWN_CLIENT = DynamicCommandExceptionType { value: Any? -> LiteralMessage("Unknown client '$value'") }

        fun client(networkServer: NetworkServer): ClientArgument {
            return ClientArgument(networkServer)
        }

        fun getClient(context: CommandContext<CommandSource>, name: String): ServerNetworkContext {
            return context.getArgument(name, ServerNetworkContext::class.java)
        }
    }
}