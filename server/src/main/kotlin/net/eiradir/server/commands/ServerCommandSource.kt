package net.eiradir.server.commands

import net.eiradir.server.commands.CommandSource
import net.eiradir.server.network.NetworkServer

interface ServerCommandSource : CommandSource {
    val networkServer: NetworkServer
}