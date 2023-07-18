package net.eiradir.server.pathfinding.astar

import net.eiradir.server.pathfinding.IGridNode
import kotlin.math.abs

object GridBasedHeuristic : IHeuristic<IGridNode> {

    override fun estimate(node: IGridNode, endNode: IGridNode): Float {
        return (abs(endNode.gridPosition.x - node.gridPosition.x) + abs(endNode.gridPosition.y - node.gridPosition.y)).toFloat()
    }
}
