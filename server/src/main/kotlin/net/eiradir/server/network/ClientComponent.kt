package net.eiradir.server.network

import com.badlogic.ashley.core.Component

data class ClientComponent(val client: ServerNetworkContext) : Component {
    var status: ConnectionStatus = ConnectionStatus.PRE_AUTH
}