package net.eiradir.server.entity.network

import net.eiradir.server.entity.NetworkedEntity
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet

data class EntityAddPacket(val mapName: String, val entity: NetworkedEntity) : Packet {

    companion object {
        fun decode(buf: SupportedInput): EntityAddPacket {
            val name = buf.readString()
            val entity = buf.readEntity()
            return EntityAddPacket(name, entity)
        }

        fun encode(buf: SupportedOutput, packet: EntityAddPacket) {
            buf.writeString(packet.mapName)
            buf.writeEntity(packet.entity)
        }
    }
}