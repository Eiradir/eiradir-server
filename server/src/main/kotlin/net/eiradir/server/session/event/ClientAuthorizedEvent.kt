package net.eiradir.server.session.event

import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.session.PlayerSession

data class ClientAuthorizedEvent(val client: ServerNetworkContext, val session: PlayerSession)
