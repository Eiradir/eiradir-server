package net.eiradir.server.pathfinding

import com.badlogic.gdx.math.Vector3
import java.util.stream.Stream

abstract class CombinedNodeGraph<TKey, TNode> : INodeGraph<TKey, TNode> {
    override fun getConnections(graph: INodeGraph<TKey, TNode>, fromNode: TNode): Stream<IConnection<TNode>> {
        val position = getNodePosition(fromNode)
        val effectiveGraph = getGraphAt(position)
        return effectiveGraph?.getConnections(this, fromNode) ?: Stream.empty()
    }

    override fun getNodeAt(position: Vector3, searchMode: NodeSearchMode): TNode? {
        val graph = getGraphAt(position)
        return graph?.getNodeAt(position, searchMode)
    }

    override fun isValidEndNode(node: TNode): Boolean {
        val graph = getGraphAt(getNodePosition(node))
        return graph?.isValidEndNode(node) ?: false
    }

    abstract fun getGraphAt(position: Vector3): INodeGraph<TKey, TNode>?
    abstract fun getNodePosition(node: TNode): Vector3
}