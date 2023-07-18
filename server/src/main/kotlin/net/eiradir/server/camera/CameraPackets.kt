package net.eiradir.server.camera

import net.eiradir.server.network.packets.PacketFactory
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.AdminRoles
import net.eiradir.server.network.NetworkServerClient
import net.eiradir.server.network.ServerNetworkContext

class CameraPackets(packets: PacketFactory, private val cameraService: CameraService) : Initializer {

    init {
        packets.registerPacket(CameraSetPositionPacket::class, CameraSetPositionPacket::encode, CameraSetPositionPacket::decode)
        packets.registerPacket(CameraFollowEntityPacket::class, CameraFollowEntityPacket::encode, CameraFollowEntityPacket::decode)

        packets.registerPacketHandler(CameraSetPositionPacket::class) { context, packet ->
            val connection = (context as ServerNetworkContext).connectionEntity ?: return@registerPacketHandler
            context.requireRole(AdminRoles.OBSERVE, NetworkServerClient::illegalPacket) {
                cameraService.setCameraPositionWithoutUpdate(connection, packet.position)
            }
        }
    }

}
