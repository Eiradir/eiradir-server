package net.eiradir.server.interact

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet

data class InteractPacket(val interaction: Interaction, val params: ByteBuf) : Packet {

    companion object {
        fun decode(buf: SupportedInput): InteractPacket {
            val interaction = buf.readFromRegistry { it.interactions }
            val len = buf.readShort().toInt()
            val data = buf.readBytes(ByteArray(len))
            return InteractPacket(interaction, Unpooled.wrappedBuffer(data))
        }

        fun encode(buf: SupportedOutput, packet: InteractPacket) {
            buf.writeId(packet.interaction)
            buf.writeShort(packet.params.readableBytes())
            buf.writeBytes(packet.params.array(), 0, packet.params.readableBytes())
        }
    }
}