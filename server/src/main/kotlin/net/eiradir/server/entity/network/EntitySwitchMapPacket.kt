package net.eiradir.server.entity.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class EntitySwitchMapPacket(val entityId: UUID, val mapName: String) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntitySwitchMapPacket {
            val entityId = buf.readUniqueId()
            val mapName = buf.readString()
            return EntitySwitchMapPacket(entityId, mapName)
        }

        fun encode(buf: SupportedOutput, packet: EntitySwitchMapPacket) {
            buf.writeUniqueId(packet.entityId)
            buf.writeString(packet.mapName)
        }
    }
}