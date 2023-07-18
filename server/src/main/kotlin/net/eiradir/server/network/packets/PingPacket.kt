package net.eiradir.server.network.packets

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput

data class PingPacket(val challenge: Long, val lastLatency: Short) : Packet {
    companion object {
        fun encode(buf: SupportedOutput, packet: PingPacket) {
            buf.writeLong(packet.challenge)
            buf.writeShort(packet.lastLatency.toInt())
        }

        fun decode(buf: SupportedInput): PingPacket {
            val challenge = buf.readLong()
            val lastPing = buf.readShort()
            return PingPacket(challenge, lastPing)
        }
    }
}