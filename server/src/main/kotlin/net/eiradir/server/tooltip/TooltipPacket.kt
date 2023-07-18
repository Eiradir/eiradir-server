package net.eiradir.server.tooltip

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

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