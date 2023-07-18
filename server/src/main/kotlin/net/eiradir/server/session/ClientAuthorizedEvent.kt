package net.eiradir.server.session

import net.eiradir.server.network.ServerNetworkContext

data class ClientAuthorizedEvent(val client: ServerNetworkContext, val session: PlayerSession)
