package net.eiradir.server.hud.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.packets.Packet

data class HudInventoryPacket(val hudId: Int, val key: Int, val items: List<ItemInstance>) : Packet {

    companion object {
        fun decode(buf: SupportedInput): HudInventoryPacket {
            val hudId = buf.readInt()
            val key = buf.readByte().toInt()
            val size = buf.readVarInt()
            val items = List(size) {
                buf.readItemInstance()
            }
            return HudInventoryPacket(hudId, key, items)
        }

        fun encode(buf: SupportedOutput, packet: HudInventoryPacket) {
            buf.writeInt(packet.hudId)
            buf.writeByte(packet.key)
            buf.writeVarInt(packet.items.size)
            packet.items.forEach {
                buf.writeItemInstance(it)
            }
        }
    }
}