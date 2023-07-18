package net.eiradir.server.menu

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

data class MenuPacket(val nonce: Int, val menu: Menu) : Packet {

    companion object {
        fun decode(buf: SupportedInput): MenuPacket {
            throw UnsupportedOperationException()
        }

        fun encode(buf: SupportedOutput, packet: MenuPacket) {
            buf.writeInt(packet.nonce)
            packet.menu.encode(buf)
        }
    }
}