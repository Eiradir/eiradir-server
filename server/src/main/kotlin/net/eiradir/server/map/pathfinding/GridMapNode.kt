package net.eiradir.server.map.pathfinding

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.pathfinding.BlockedGridDirection
import net.eiradir.server.pathfinding.IGridNode

data class GridMapNode(val position: Vector3, val blockedDirections: Int = BlockedGridDirection.None) : IGridNode {
    val isWalkable: Boolean = true

    override val gridPosition get() = position.floorToIntVector()
}