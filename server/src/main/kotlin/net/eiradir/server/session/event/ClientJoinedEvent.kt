package net.eiradir.server.session.event

import net.eiradir.server.network.ServerNetworkContext

data class ClientJoinedEvent(val client: ServerNetworkContext, val properties: Map<String, String>)
