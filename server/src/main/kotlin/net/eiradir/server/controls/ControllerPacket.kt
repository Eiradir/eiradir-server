package net.eiradir.server.controls

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class ControllerPacket(val entityId: UUID, val type: ControllerType, val seat: Int) : Packet {

    companion object {
        fun decode(buf: SupportedInput): ControllerPacket {
            return ControllerPacket(buf.readUniqueId(), buf.readEnum(), buf.readByte().toInt())
        }

        fun encode(buf: SupportedOutput, packet: ControllerPacket) {
            buf.writeUniqueId(packet.entityId)
            buf.writeEnum(packet.type)
            buf.writeByte(packet.seat)
        }
    }
}