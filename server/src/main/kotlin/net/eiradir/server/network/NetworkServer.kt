package net.eiradir.server.network

import io.netty.channel.ChannelFuture
import net.eiradir.server.network.NetworkContext

interface NetworkServer {
    fun start(port: Int): ChannelFuture
    fun processTasks()
    fun stop()

    val clientCount: Int
    val clients: Collection<NetworkContext>
}