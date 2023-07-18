package net.eiradir.server.controls

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet

data class MoveInputPacket(val position: Vector3Int) : Packet {

    companion object {
        fun decode(buf: SupportedInput): MoveInputPacket {
            return MoveInputPacket(buf.readVector3Int())
        }

        fun encode(buf: SupportedOutput, packet: MoveInputPacket) {
            buf.writeVector3Int(packet.position)
        }
    }
}