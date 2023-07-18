package net.eiradir.server.interact

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet

data class TileInteractPacket(val position: Vector3Int, val interaction: Interaction, val params: ByteBuf) : Packet {

    companion object {
        fun decode(buf: SupportedInput): TileInteractPacket {
            val position = buf.readVector3Int()
            val interaction = buf.readFromRegistry { it.interactions }
            val len = buf.readShort().toInt()
            val data = buf.readBytes(ByteArray(len))
            return TileInteractPacket(position, interaction, Unpooled.wrappedBuffer(data))
        }

        fun encode(buf: SupportedOutput, packet: TileInteractPacket) {
            buf.writeVector3Int(packet.position)
            buf.writeId(packet.interaction)
            buf.writeShort(packet.params.readableBytes())
            buf.writeBytes(packet.params.array(), 0, packet.params.readableBytes())
        }
    }
}