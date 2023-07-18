package net.eiradir.server.hud.network

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

data class HudPropertyPacket(val hudId: Int, val key: Int, val data: ByteBuf) : Packet {

    companion object {
        fun decode(buf: SupportedInput): HudPropertyPacket {
            val hudId = buf.readInt()
            val key = buf.readByte().toInt()
            val len = buf.readShort().toInt()
            val data = buf.readBytes(ByteArray(len))
            return HudPropertyPacket(hudId, key, Unpooled.wrappedBuffer(data))
        }

        fun encode(buf: SupportedOutput, packet: HudPropertyPacket) {
            buf.writeInt(packet.hudId)
            buf.writeByte(packet.key)
            buf.writeShort(packet.data.readableBytes())
            buf.writeBytes(packet.data.array(), 0, packet.data.readableBytes())
        }
    }
}