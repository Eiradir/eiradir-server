package net.eiradir.server.status

import net.eiradir.server.config.ServerConfig
import net.eiradir.server.network.NetworkServer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ServerStatusProviderImpl(
    private val networkServer: NetworkServer,
    private val config: ServerConfig
) : ServerStatusProvider {
    override val serverName: String get() = config.name
    override val onlinePlayerCount: Int get() = networkServer.clientCount // TODO should be player count in the future, to only show fully connected ones
}