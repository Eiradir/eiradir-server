package net.eiradir.server.camera.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import java.util.UUID

/**
 * Configures the client's camera to follow a specific entity by its id.
 */
data class CameraFollowEntityPacket(val entityId: UUID) : Packet {

    companion object {
        fun decode(buf: SupportedInput): CameraFollowEntityPacket {
            val entityId = buf.readUniqueId()
            return CameraFollowEntityPacket(entityId)
        }

        fun encode(buf: SupportedOutput, packet: CameraFollowEntityPacket) {
            buf.writeUniqueId(packet.entityId)
        }
    }
}