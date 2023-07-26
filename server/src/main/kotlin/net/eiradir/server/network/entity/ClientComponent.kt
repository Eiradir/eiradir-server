package net.eiradir.server.network.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.network.ConnectionStatus
import net.eiradir.server.network.ServerNetworkContext

data class ClientComponent(val client: ServerNetworkContext) : Component {
    var status: ConnectionStatus = ConnectionStatus.PRE_AUTH
}