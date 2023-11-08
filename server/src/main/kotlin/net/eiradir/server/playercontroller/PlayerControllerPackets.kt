package net.eiradir.server.playercontroller

import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.mobility.Mobility
import net.eiradir.server.mobility.QueuedMoveInput
import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.network.ServerNetworkContext

class PlayerControllerPackets(packets: PacketFactory, playerControllerService: PlayerControllerService) : Initializer {

    private val transformMapper = mapperFor<GridTransform>()
    private val mobilityMapper = mapperFor<Mobility>()

    init {
        packets.registerPacket(ControllerPacket::class, ControllerPacket::encode, ControllerPacket::decode)
        packets.registerPacket(MoveInputPacket::class, MoveInputPacket::encode, MoveInputPacket::decode)
        packets.registerPacket(TurnInputPacket::class, TurnInputPacket::encode, TurnInputPacket::decode)

        packets.registerPacketHandler(MoveInputPacket::class) { context, packet ->
            val connection = (context as? ServerNetworkContext)?.connectionEntity ?: return@registerPacketHandler
            val entity = playerControllerService.getControlledEntity(connection) ?: return@registerPacketHandler
            val transform = transformMapper[entity] ?: return@registerPacketHandler
            val mobility = mobilityMapper[entity]
            if (mobility != null) {
                mobilityMapper[entity]?.moveQueue?.add(QueuedMoveInput(packet.position))
            } else {
                transform.direction = transform.position.directionTo(packet.position)
                transform.lastDirection = transform.direction
                transform.position = packet.position
            }
        }

        packets.registerPacketHandler(TurnInputPacket::class) { context, packet ->
            val connection = (context as? ServerNetworkContext)?.connectionEntity ?: return@registerPacketHandler
            val entity = playerControllerService.getControlledEntity(connection) ?: return@registerPacketHandler
            transformMapper[entity]?.direction = packet.direction
        }
    }
}