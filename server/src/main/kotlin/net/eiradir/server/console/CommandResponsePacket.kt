package net.eiradir.server.console

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

data class CommandResponsePacket(val response: String) : Packet {

    companion object {
        fun decode(buf: SupportedInput): CommandResponsePacket {
            val response = buf.readString()
            return CommandResponsePacket(response)
        }

        fun encode(buf: SupportedOutput, packet: CommandResponsePacket) {
            buf.writeString(packet.response)
        }
    }
}