package net.eiradir.server.entity.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.io.readEnum
import net.eiradir.server.io.writeEnum
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class EntityDirectionPacket(val entityId: UUID, val direction: GridDirection) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntityDirectionPacket {
            val entityId = buf.readUniqueId()
            val direction = buf.readEnum<GridDirection>()
            return EntityDirectionPacket(entityId, direction)
        }

        fun encode(buf: SupportedOutput, packet: EntityDirectionPacket) {
            buf.writeUniqueId(packet.entityId)
            buf.writeEnum(packet.direction)
        }
    }
}