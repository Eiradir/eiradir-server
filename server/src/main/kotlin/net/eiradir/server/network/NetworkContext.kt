package net.eiradir.server.network

import arrow.core.Either
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import net.eiradir.server.network.packets.Packet
import java.net.InetSocketAddress

interface NetworkContext {
    val channel: Channel
    val address: InetSocketAddress
    fun send(packet: Packet): ChannelFuture
    fun respond(packet: Packet): ChannelFuture
    fun disconnect(): Either<NetworkError, ChannelFuture>
    fun scheduleTask(function: () -> Unit)
}