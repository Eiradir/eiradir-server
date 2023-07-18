package net.eiradir.server.commands

import com.mojang.brigadier.context.CommandContext
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.network.ServerNetworkContext

sealed interface CommandSourceProvider {
    fun get(context: CommandContext<CommandSource>): CommandSource?
    fun toString(context: CommandContext<CommandSource>): String

    class Server(private val serverCommandSource: ServerCommandSource) : CommandSourceProvider {
        override fun get(context: CommandContext<CommandSource>): CommandSource {
            return serverCommandSource
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return "server"
        }
    }

    class ByUsername(val username: String) : CommandSourceProvider {
        override fun get(context: CommandContext<CommandSource>): CommandSource? {
            val networkServer = (context.source as? ServerCommandSource)?.networkServer ?: return null
            val client = networkServer.clients.asSequence().map { it as ServerNetworkContext }.firstOrNull { it.session?.username == username }
            return client?.let { NetworkClientCommandSource(it, context.source.mapView) }
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return username
        }
    }

    object ByCursorPosition : CommandSourceProvider {
        override fun get(context: CommandContext<CommandSource>): CommandSource? {
            val mapView = context.source.mapView
            val entities = mapView.getEntitiesAt(context.source.cursorPosition)
            val entity = entities.firstOrNull() ?: return null
            return EntityCommandSourceImpl(entity, mapView)
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return "at cursor position ${context.source.cursorPosition}"
        }
    }

    object BySelection : CommandSourceProvider {
        override fun get(context: CommandContext<CommandSource>): CommandSource? {
            val mapView = context.source.mapView
            val id = context.source.selectedEntityId ?: return null
            val entity = mapView.getEntityById(id) ?: return null
            return EntityCommandSourceImpl(entity, mapView)
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return "by selection (${context.source.selectedEntityId})"
        }
    }
}