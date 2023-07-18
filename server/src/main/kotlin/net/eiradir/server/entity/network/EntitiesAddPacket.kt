package net.eiradir.server.entity.network

import net.eiradir.server.entity.NetworkedEntity
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.map.ChunkDimensions

data class EntitiesAddPacket(val mapName: String, val chunkPos: ChunkDimensions, val entities: List<NetworkedEntity>) : Packet {

    companion object {
        const val MAX_ENTITIES_PER_PACKET = 250

        fun decode(buf: SupportedInput): EntitiesAddPacket {
            val name = buf.readString()
            val chunkPos = buf.readChunkDimensions()
            val count = buf.readVarInt()
            val entities = mutableListOf<NetworkedEntity>()
            for (i in 0 until count) {
                entities.add(buf.readEntity())
            }
            return EntitiesAddPacket(name, chunkPos, entities)
        }

        fun encode(buf: SupportedOutput, packet: EntitiesAddPacket) {
            buf.writeString(packet.mapName)
            buf.writeChunkDimensions(packet.chunkPos)
            buf.writeVarInt(packet.entities.size)
            for (entity in packet.entities) {
                buf.writeEntity(entity)
            }
        }
    }

    override fun toString(): String {
        return "EntitiesAddPacket(mapName='$mapName', chunkPos=$chunkPos, entities=${entities.size})"
    }
}