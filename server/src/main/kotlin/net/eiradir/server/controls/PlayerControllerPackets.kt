package net.eiradir.server.controls

import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.network.ServerNetworkContext

class PlayerControllerPackets(packets: PacketFactory, playerControllerService: PlayerControllerService) : Initializer {

    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()

    init {
        packets.registerPacket(ControllerPacket::class, ControllerPacket::encode, ControllerPacket::decode)
        packets.registerPacket(MoveInputPacket::class, MoveInputPacket::encode, MoveInputPacket::decode)
        packets.registerPacket(TurnInputPacket::class, TurnInputPacket::encode, TurnInputPacket::decode)

        packets.registerPacketHandler(MoveInputPacket::class) { context, packet ->
            val connection = (context as? ServerNetworkContext)?.connectionEntity ?: return@registerPacketHandler
            val entity = playerControllerService.getControlledEntity(connection) ?: return@registerPacketHandler
            transformMapper[entity]?.position = packet.position
        }

        packets.registerPacketHandler(TurnInputPacket::class) { context, packet ->
            val connection = (context as? ServerNetworkContext)?.connectionEntity ?: return@registerPacketHandler
            val entity = playerControllerService.getControlledEntity(connection) ?: return@registerPacketHandler
            transformMapper[entity]?.direction = packet.direction
        }
    }
}