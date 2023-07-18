package net.eiradir.server.map.event

import net.eiradir.server.data.Tile
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap

data class TileUpdatedEvent(
    val map: EiradirMap,
    val chunkPos: ChunkDimensions,
    val position: Vector3Int,
    val tile: Tile?
)