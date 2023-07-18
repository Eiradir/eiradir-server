package net.eiradir.server.hud.network

import net.eiradir.server.hud.HudState
import net.eiradir.server.hud.HudType
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.network.packets.Packet

data class HudStatePacket(val hudId: Int, val type: HudType, val state: HudState) : Packet {

    companion object {
        fun decode(buf: SupportedInput): HudStatePacket {
            val hudId = buf.readInt()
            val type = buf.readFromRegistry { it.hudTypes }
            val state = buf.readEnum<HudState>()
            return HudStatePacket(hudId, type, state)
        }

        fun encode(buf: SupportedOutput, packet: HudStatePacket) {
            buf.writeInt(packet.hudId)
            buf.writeId(packet.type)
            buf.writeEnum(packet.state)
        }
    }
}