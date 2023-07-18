package net.eiradir.server.pathfinding

import net.eiradir.server.math.GridDirection

object BlockedGridDirection {
    const val None = 0
    const val SouthEast = 1
    const val South = 2
    const val SouthWest = 4
    const val East = 8
    const val West = 16
    const val NorthEast = 32
    const val North = 64
    const val NorthWest = 128
    const val All = SouthEast or South or SouthWest or East or West or NorthEast or North or NorthWest

    fun isBlocked(blockedDirections: Int, direction: GridDirection): Boolean {
        return when (direction) {
            GridDirection.SouthEast -> (blockedDirections and SouthEast) == SouthEast
            GridDirection.South -> (blockedDirections and South) == South
            GridDirection.SouthWest -> (blockedDirections and SouthWest) == SouthWest
            GridDirection.East -> (blockedDirections and East) == East
            GridDirection.West -> (blockedDirections and West) == West
            GridDirection.NorthEast -> (blockedDirections and NorthEast) == NorthEast
            GridDirection.North -> (blockedDirections and North) == North
            GridDirection.NorthWest -> (blockedDirections and NorthWest) == NorthWest
            else -> false
        }
    }
}