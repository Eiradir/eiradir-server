package net.eiradir.server.network.event

import net.eiradir.server.network.ServerNetworkContext

data class ClientDisconnectedEvent(val client: ServerNetworkContext)