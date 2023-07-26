package net.eiradir.server.network

import com.badlogic.ashley.core.Engine
import com.google.common.eventbus.EventBus
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.timeout.ReadTimeoutHandler
import net.eiradir.server.entity.AdvancedEncoders
import net.eiradir.server.extensions.logger
import net.eiradir.server.network.event.ClientConnectedEvent
import net.eiradir.server.network.event.ClientDisconnectedEvent
import net.eiradir.server.network.event.NetworkRegisterHandlersEvent
import net.eiradir.server.network.event.NetworkRegisterPacketsEvent
import net.eiradir.server.network.packets.PacketDecoder
import net.eiradir.server.network.packets.PacketEncoder
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.network.packets.PingPacket
import net.eiradir.server.registry.Registries
import java.util.concurrent.ConcurrentLinkedQueue

@ChannelHandler.Sharable
internal class NetworkServerImpl(
    private val packetFactory: PacketFactory,
    private val engine: Engine,
    private val eventBus: EventBus,
    private val registries: Registries,
    private val advancedEncoders: AdvancedEncoders
) : ChannelInboundHandlerAdapter(), NetworkServer {

    private val log = logger()

    private val networkReadTimeout: Int = 60000
    private val maxFrameLength = Short.MAX_VALUE.toInt()
    private val lengthFieldLength = 2

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
    private var channel: Channel? = null

    private val _clients = ConcurrentLinkedQueue<NetworkServerClient>()
    override val clients: Collection<NetworkContext> = _clients

    override fun start(port: Int): ChannelFuture {
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    val client = NetworkServerClient(packetFactory, channel)
                    channel.attr(NetworkServerClient.ATTRIBUTE).set(client)
                    _clients.add(client)
                    channel.pipeline()
                        .addLast("timeout", ReadTimeoutHandler(networkReadTimeout))
                        .addLast("server", this@NetworkServerImpl)
                        .addLast(
                            "frameDecoder",
                            LengthFieldBasedFrameDecoder(
                                maxFrameLength,
                                0,
                                lengthFieldLength,
                                0,
                                lengthFieldLength,
                                true
                            )
                        )
                        .addLast("decoder", PacketDecoder(packetFactory, registries, advancedEncoders))
                        .addLast("packetHandler", client)
                        .addLast("framePrepender", LengthFieldPrepender(lengthFieldLength, 0, false))
                        .addLast("encoder", PacketEncoder(packetFactory, registries, advancedEncoders))

                    client.addToEngine(engine)
                    eventBus.post(ClientConnectedEvent(client))
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)

        packetFactory.registerPacket(PingPacket::class, PingPacket::encode, PingPacket::decode)
        packetFactory.registerPacketHandler(PingPacket::class, ::handlePingPacket)
        eventBus.post(NetworkRegisterPacketsEvent(packetFactory))
        eventBus.post(NetworkRegisterHandlersEvent(packetFactory))

        return bootstrap.bind(port).addListener {
            if (it.isSuccess) {
                log.info("Server is listening on port $port")
                Thread({
                    var last = System.currentTimeMillis()
                    while (!bossGroup.isShutdown) {
                        val now = System.currentTimeMillis()
                        val delta = (now - last).toInt()
                        last = now

                        _clients.forEach { client ->
                            client.processPackets(delta)
                        }

                        Thread.sleep(10)
                    }
                }, "NetworkServer").start()
            } else {
                log.error("Failed to start server on port $port", it.cause())
            }
        }
    }

    private fun handlePingPacket(context: NetworkContext, packet: PingPacket) {
        (context as NetworkServerClient).handlePingPacket(packet)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        val client = ctx.channel().attr(NetworkServerClient.ATTRIBUTE).get()
        _clients.remove(client)
        client.removeFromEngine(engine)
        log.info("Client disconnected: ${client.address}")
        eventBus.post(ClientDisconnectedEvent(client))
    }

    override fun processTasks() {
        _clients.forEach { client ->
            client.processTasks()
        }
    }

    override fun stop() {
        channel?.close()
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }

    override val clientCount: Int get() = clients.size
}