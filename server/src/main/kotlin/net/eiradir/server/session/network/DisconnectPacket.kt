package net.eiradir.server.session.network

import net.eiradir.server.network.DisconnectReason
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.network.packets.Packet

data class DisconnectPacket(val reason: DisconnectReason, val message: String) : Packet {

    companion object {
        fun decode(buf: SupportedInput): DisconnectPacket {
            val reason = buf.readEnum<DisconnectReason>()
            val message = buf.readString()
            return DisconnectPacket(reason, message)
        }

        fun encode(buf: SupportedOutput, packet: DisconnectPacket) {
            buf.writeEnum(packet.reason)
            buf.writeString(packet.message)
        }
    }
}