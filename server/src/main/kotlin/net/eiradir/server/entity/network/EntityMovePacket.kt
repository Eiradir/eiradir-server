package net.eiradir.server.entity.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet
import java.util.UUID

data class EntityMovePacket(val entityId: UUID, val position: Vector3Int) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntityMovePacket {
            val entityId = buf.readUniqueId()
            val position = buf.readVector3Int()
            return EntityMovePacket(entityId, position)
        }

        fun encode(buf: SupportedOutput, packet: EntityMovePacket) {
            buf.writeUniqueId(packet.entityId)
            buf.writeVector3Int(packet.position)
        }
    }
}