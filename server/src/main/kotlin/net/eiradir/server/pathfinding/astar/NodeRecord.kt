package net.eiradir.server.pathfinding.astar

import com.badlogic.gdx.utils.BinaryHeap
import net.eiradir.server.pathfinding.IConnection

class NodeRecord<TNode> : BinaryHeap.Node(0f) {
    var node: TNode? = null
    var connection: IConnection<TNode>? = null
    var costSoFar: Float = 0f
    var status: NodeStatus = NodeStatus.Unvisited
    var searchId: Int = 0

    val estimatedTotalCost get() = this.value
}