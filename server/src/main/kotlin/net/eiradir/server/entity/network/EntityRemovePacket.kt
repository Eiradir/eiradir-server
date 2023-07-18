package net.eiradir.server.entity.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class EntityRemovePacket(val mapName: String, val entityId: UUID) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntityRemovePacket {
            val name = buf.readString()
            val entityId = buf.readUniqueId()
            return EntityRemovePacket(name, entityId)
        }

        fun encode(buf: SupportedOutput, packet: EntityRemovePacket) {
            buf.writeString(packet.mapName)
            buf.writeUniqueId(packet.entityId)
        }
    }
}