package net.eiradir.server.session.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

data class ConnectPacket(val username: String, val token: String, val properties: Map<String, String>) : Packet {

    companion object {
        fun decode(buf: SupportedInput): ConnectPacket {
            val username = buf.readString()
            val loginToken = buf.readString()
            val properties = mutableMapOf<String, String>()
            val propertyCount = buf.readVarInt()
            for (i in 0 until propertyCount) {
                val key = buf.readString()
                val value = buf.readString()
                properties[key] = value
            }
            return ConnectPacket(username, loginToken, properties)
        }

        fun encode(buf: SupportedOutput, packet: ConnectPacket) {
            buf.writeString(packet.username)
            buf.writeString(packet.token)
            buf.writeVarInt(packet.properties.size)
            for ((key, value) in packet.properties) {
                buf.writeString(key)
                buf.writeString(value)
            }
        }
    }
}