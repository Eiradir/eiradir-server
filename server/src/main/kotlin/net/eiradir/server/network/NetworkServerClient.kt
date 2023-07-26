package net.eiradir.server.network

import arrow.core.Either
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.netty.channel.*
import io.netty.util.AttributeKey
import ktx.ashley.entity
import net.eiradir.server.extensions.add
import net.eiradir.server.extensions.logger
import net.eiradir.server.extensions.marker
import net.eiradir.server.network.entity.ClientComponent
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.network.packets.PingPacket
import net.eiradir.server.session.network.DisconnectPacket
import net.eiradir.server.session.PlayerSession
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

class NetworkServerClient(private val packetFactory: PacketFactory, override val channel: Channel) : ChannelInboundHandlerAdapter(), ChannelFutureListener,
    ServerNetworkContext {

    private val log = logger()
    private val marker = marker("PACKET")

    private val incomingPackets = ConcurrentLinkedQueue<Packet>()
    private var timeSinceLastPing = 0
    private var pingSent = false
    private var pingSentAt = 0L
    private var latency = 0
    override var session: PlayerSession? = null
    override var connectionEntity: Entity? = null; private set
    override var loadedEntity: Entity? = null
    private var lastHandledPacket: Packet? = null
    private val tasks = ConcurrentLinkedQueue<() -> Unit>()

    override val address: InetSocketAddress get() = channel.remoteAddress() as InetSocketAddress

    override fun send(packet: Packet): ChannelFuture {
        if (packet !is PingPacket) {
            log.debug(marker, "Sending packet {}", packet)
        }
        return channel.writeAndFlush(packet).addListener(this)
    }

    override fun respond(packet: Packet): ChannelFuture {
        return send(packet)
    }

    override fun disconnect(): Either<NetworkError, ChannelFuture> {
        if (channel.isRegistered) {
            return Either.Right(channel.close())
        } else {
            return Either.Left(NetworkError.NotConnected)
        }
    }


    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        incomingPackets.add(msg as Packet)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable) {
        log.error("Network client error", cause)
        disconnect()
    }

    override fun operationComplete(future: ChannelFuture) {
        if (!future.isSuccess) {
            exceptionCaught(null, future.cause())
        }
    }

    fun processPackets(delta: Int) {
        timeSinceLastPing += delta
        if (timeSinceLastPing >= 15000) {
            sendPingPacket()
            timeSinceLastPing = 0
        }

        var packet = incomingPackets.poll()
        while (packet != null) {
            try {
                lastHandledPacket = packet
                val handler = packetFactory.getPacketHandler(packet)
                if (packet !is PingPacket) {
                    log.debug(marker, "Received packet $packet")
                }
                handler(this, packet)
                packet = incomingPackets.poll()
            } catch (e: Exception) {
                exceptionCaught(null, e)
            }
        }
    }

    private fun sendPingPacket() {
        if (!pingSent) {
            pingSent = true
            pingSentAt = System.currentTimeMillis()
            send(PingPacket(pingSentAt, latency.toShort()))
        } else {
            log.warn("Timeout waiting for pong from $address, disconnecting")
            disconnect()
        }
    }

    fun handlePingPacket(packet: PingPacket) {
        if (pingSent && packet.challenge == pingSentAt) {
            pingSent = false
            val currentLatency = (System.currentTimeMillis() - pingSentAt).toInt()
            if (latency == 0) {
                latency = currentLatency
            } else {
                latency = (latency * 3 + currentLatency) / 4
            }
        } else {
            log.warn("Invalid pong from $address, disconnecting")
            disconnect()
        }
    }

    override fun addToEngine(engine: Engine) {
        connectionEntity = engine.entity {
            add(ClientComponent(this@NetworkServerClient))
        }
    }

    override fun removeFromEngine(engine: Engine) {
        connectionEntity?.let { engine.removeEntity(it) }
        connectionEntity = null
    }

    companion object {
        private val log = logger()
        val ATTRIBUTE: AttributeKey<NetworkServerClient> = AttributeKey.valueOf("eiradir:client")

        fun illegalPacket(client: ServerNetworkContext) {
            log.warn("Kicking client ${client.address} (${client.session?.username}) for illegal packet ${(client as? NetworkServerClient)?.lastHandledPacket}")
            client.send(DisconnectPacket(DisconnectReason.FORBIDDEN, "Illegal packet"))
            client.disconnect()
        }
    }

    override fun scheduleTask(function: () -> Unit) {
        tasks.add(function)
    }

    fun processTasks() {
        var task = tasks.poll()
        while (task != null) {
            try {
                task()
                task = tasks.poll()
            } catch (e: Exception) {
                exceptionCaught(null, e)
            }
        }
    }
}