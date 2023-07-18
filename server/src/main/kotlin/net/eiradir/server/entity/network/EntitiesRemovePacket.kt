package net.eiradir.server.entity.network

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.map.ChunkDimensions
import java.util.UUID

data class EntitiesRemovePacket(val mapName: String, val chunkPos: ChunkDimensions, val entityIds: List<UUID>) : Packet {

    companion object {
        const val MAX_ENTITIES_PER_PACKET = 400

        fun decode(buf: SupportedInput): EntitiesRemovePacket {
            val name = buf.readString()
            val chunkPos = buf.readChunkDimensions()
            val count = buf.readVarInt()
            val entities = mutableListOf<UUID>()
            for (i in 0 until count) {
                entities.add(buf.readUniqueId())
            }
            return EntitiesRemovePacket(name, chunkPos, entities)
        }

        fun encode(buf: SupportedOutput, packet: EntitiesRemovePacket) {
            buf.writeString(packet.mapName)
            buf.writeChunkDimensions(packet.chunkPos)
            buf.writeVarInt(packet.entityIds.size)
            for (entity in packet.entityIds) {
                buf.writeUniqueId(entity)
            }
        }
    }

    override fun toString(): String {
        return "EntitiesRemovePacket(mapName='$mapName', chunkPos=$chunkPos, entities=${entityIds.size})"
    }
}