package net.eiradir.server.camera

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet

/**
 * Moves the client's camera to a specific position. Can be sent by both server and client. Requires 'observe' permission when sent from a client.
 */
data class CameraSetPositionPacket(val position: Vector3Int) : Packet {

    companion object {
        fun decode(buf: SupportedInput): CameraSetPositionPacket {
            val position = buf.readVector3Int()
            return CameraSetPositionPacket(position)
        }

        fun encode(buf: SupportedOutput, packet: CameraSetPositionPacket) {
            buf.writeVector3Int(packet.position)
        }
    }
}