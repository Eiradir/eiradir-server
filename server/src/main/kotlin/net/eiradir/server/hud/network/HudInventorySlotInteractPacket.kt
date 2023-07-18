package net.eiradir.server.hud.network

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.eiradir.server.interact.Interaction
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.packets.Packet

data class HudInventorySlotInteractPacket(val hudId: Int, val key: Int, val slotId: Int, val interaction: Interaction, val params: ByteBuf) : Packet {

    companion object {
        fun decode(buf: SupportedInput): HudInventorySlotInteractPacket {
            val hudId = buf.readInt()
            val key = buf.readByte().toInt()
            val slotId = buf.readByte().toInt()
            val interaction = buf.readFromRegistry { it.interactions }
            val len = buf.readShort().toInt()
            val data = buf.readBytes(ByteArray(len))
            return HudInventorySlotInteractPacket(hudId, key, slotId, interaction, Unpooled.wrappedBuffer(data))
        }

        fun encode(buf: SupportedOutput, packet: HudInventorySlotInteractPacket) {
            buf.writeInt(packet.hudId)
            buf.writeByte(packet.key)
            buf.writeByte(packet.slotId)
            buf.writeId(packet.interaction)
            buf.writeShort(packet.params.readableBytes())
            buf.writeBytes(packet.params.array(), 0, packet.params.readableBytes())
        }
    }
}