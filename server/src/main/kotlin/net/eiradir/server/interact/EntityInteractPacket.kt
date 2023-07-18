package net.eiradir.server.interact

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class EntityInteractPacket(val entityId: UUID, val interaction: Interaction, val params: ByteBuf) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntityInteractPacket {
            val entityId = buf.readUniqueId()
            val interaction = buf.readFromRegistry { it.interactions }
            val len = buf.readShort().toInt()
            val data = buf.readBytes(ByteArray(len))
            return EntityInteractPacket(entityId, interaction, Unpooled.wrappedBuffer(data))
        }

        fun encode(buf: SupportedOutput, packet: EntityInteractPacket) {
            buf.writeUniqueId(packet.entityId)
            buf.writeId(packet.interaction)
            buf.writeShort(packet.params.readableBytes())
            buf.writeBytes(packet.params.array(), 0, packet.params.readableBytes())
        }
    }
}