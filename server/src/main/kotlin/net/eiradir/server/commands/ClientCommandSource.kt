package net.eiradir.server.commands

import net.eiradir.server.commands.CommandSource
import net.eiradir.server.network.ServerNetworkContext

interface ClientCommandSource : CommandSource {
    val client: ServerNetworkContext?
}