package net.eiradir.server.tooltip.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.tooltip.Tooltip

data class TooltipPacket(val nonce: Int, val tooltip: Tooltip) : Packet {

    companion object {
        fun decode(buf: SupportedInput): TooltipPacket {
            throw UnsupportedOperationException()
        }

        fun encode(buf: SupportedOutput, packet: TooltipPacket) {
            buf.writeInt(packet.nonce)
            packet.tooltip.encode(buf)
        }
    }
}