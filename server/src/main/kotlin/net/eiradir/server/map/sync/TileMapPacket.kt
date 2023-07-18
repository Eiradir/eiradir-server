package net.eiradir.server.map.sync

import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.network.packets.Packet
import net.eiradir.server.map.ChunkDimensions

data class TileMapPacket(val name: String, val chunkPos: ChunkDimensions, val tiles: ByteArray) : Packet {

    companion object {
        fun decode(buf: SupportedInput): TileMapPacket {
            val name = buf.readString()
            val chunkPos = buf.readChunkDimensions()
            val tiles = ByteArray(chunkPos.size * chunkPos.size)
            buf.readBytes(tiles)
            return TileMapPacket(name, chunkPos, tiles)
        }

        fun encode(buf: SupportedOutput, packet: TileMapPacket) {
            buf.writeString(packet.name)
            buf.writeChunkDimensions(packet.chunkPos)
            buf.writeBytes(packet.tiles)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TileMapPacket

        if (chunkPos != other.chunkPos) return false
        if (!tiles.contentEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chunkPos.hashCode()
        result = 31 * result + tiles.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "TileMapPacket(name=$name, chunkPos=$chunkPos)"
    }
}