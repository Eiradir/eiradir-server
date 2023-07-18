package net.eiradir.server.map.sync

import net.eiradir.server.data.Tile
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.packets.Packet

data class TileUpdatePacket(val mapName: String, val position: Vector3Int, val tile: Tile?) : Packet {

    companion object {
        fun decode(buf: SupportedInput): TileUpdatePacket {
            val mapName = buf.readString()
            val position = buf.readVector3Int()
            val tile = buf.readFromRegistry { it.tiles }
            return TileUpdatePacket(mapName, position, tile)
        }

        fun encode(buf: SupportedOutput, packet: TileUpdatePacket) {
            buf.writeString(packet.mapName)
            buf.writeVector3Int(packet.position)
            buf.writeId(packet.tile)
        }
    }

}