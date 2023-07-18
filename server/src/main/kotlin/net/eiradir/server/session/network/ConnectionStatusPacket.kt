package net.eiradir.server.session.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.network.ConnectionStatus
import net.eiradir.server.network.packets.Packet

data class ConnectionStatusPacket(val status: ConnectionStatus) : Packet {

    companion object {
        fun decode(buf: SupportedInput): ConnectionStatusPacket {
            val status = buf.readEnum<ConnectionStatus>()
            return ConnectionStatusPacket(status)
        }

        fun encode(buf: SupportedOutput, packet: ConnectionStatusPacket) {
            buf.writeEnum(packet.status)
        }
    }
}