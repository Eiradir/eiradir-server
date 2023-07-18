package net.eiradir.server.map.sync

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet


data class MapUnloadPacket(val name: String) : Packet {

    companion object {
        fun decode(buf: SupportedInput): MapUnloadPacket {
            val name = buf.readString()
            return MapUnloadPacket(name)
        }

        fun encode(buf: SupportedOutput, packet: MapUnloadPacket) {
            buf.writeString(packet.name)
        }
    }

}