package net.eiradir.server.map.event

import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

data class TileChunkUpdatedEvent(
    val map: EiradirMap,
    val chunkPos: ChunkDimensions,
    val tiles: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TileChunkUpdatedEvent

        if (map != other.map) return false
        if (chunkPos != other.chunkPos) return false
        if (!tiles.contentEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = map.hashCode()
        result = 31 * result + chunkPos.hashCode()
        result = 31 * result + tiles.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "TileChunkUpdatedEvent(map=$map, chunkPos=$chunkPos)"
    }
}