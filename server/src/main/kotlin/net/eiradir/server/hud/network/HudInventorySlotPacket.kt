package net.eiradir.server.hud.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.network.packets.Packet

data class HudInventorySlotPacket(val hudId: Int, val key: Int, val slotId: Int, val item: ItemInstance) : Packet {

    companion object {
        fun decode(buf: SupportedInput): HudInventorySlotPacket {
            val hudId = buf.readInt()
            val key = buf.readByte().toInt()
            val slotId = buf.readByte().toInt()
            val item = buf.readItemInstance()
            return HudInventorySlotPacket(hudId, key, slotId, item)
        }

        fun encode(buf: SupportedOutput, packet: HudInventorySlotPacket) {
            buf.writeInt(packet.hudId)
            buf.writeByte(packet.key)
            buf.writeByte(packet.slotId)
            buf.writeItemInstance(packet.item)
        }
    }
}