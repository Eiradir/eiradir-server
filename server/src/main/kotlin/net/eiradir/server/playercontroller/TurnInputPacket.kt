package net.eiradir.server.playercontroller

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.math.GridDirection
import net.eiradir.server.network.packets.Packet

data class TurnInputPacket(val direction: GridDirection) : Packet {

    companion object {
        fun decode(buf: SupportedInput): TurnInputPacket {
            return TurnInputPacket(buf.readEnum())
        }

        fun encode(buf: SupportedOutput, packet: TurnInputPacket) {
            buf.writeEnum(packet.direction)
        }
    }
}