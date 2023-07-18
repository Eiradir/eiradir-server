package net.eiradir.server.culling

import net.eiradir.server.map.MapManager
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int

class RoomResolver(private val mapManager: MapManager) {
    fun isInsideBuilding(position: Vector3Int): Boolean {
        val positionAbove = position.offset(GridDirection.Up)
        val chunkAbove = mapManager.mergedMap.getChunkAt(positionAbove)
        return chunkAbove?.hasTileAt(positionAbove) ?: false
    }
}