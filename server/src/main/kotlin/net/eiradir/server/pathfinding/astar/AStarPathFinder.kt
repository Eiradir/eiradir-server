package net.eiradir.server.pathfinding.astar

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.BinaryHeap
import net.eiradir.server.pathfinding.INodeGraph
import net.eiradir.server.pathfinding.NodeSearchMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AStarPathFinder<TKey, TNode>(
    private val graph: INodeGraph<TKey, TNode>,
    private val costCutoff: Float,
    calculateMetrics: Boolean = false
) : IPathFinder<TNode> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val nodeRecords = mutableMapOf<TKey, NodeRecord<TNode>>()
    private val openList = BinaryHeap<NodeRecord<TNode>>()
    private val metrics = if (calculateMetrics) PathfinderMetrics() else null

    private var current: NodeRecord<TNode>? = null
    private var searchId: Int = 0

    fun searchPathSync(
        startPosition: Vector3,
        endPosition: Vector3,
        heuristic: IHeuristic<TNode>
    ): NodePath<TNode>? {
        val startNode = graph.getNodeAt(startPosition, NodeSearchMode.NearestWalkable)
        val endNode = graph.getNodeAt(endPosition, NodeSearchMode.NearestWalkable)

        if (startNode == null || endNode == null) {
            logger.warn("Could not find nodes: start = {}, end = {}", startNode, endNode)
            return null
        }

        return searchNodePathSync(startNode, endNode, heuristic)
    }

    override fun searchNodePathSync(startNode: TNode, endNode: TNode, heuristic: IHeuristic<TNode>): NodePath<TNode>? {
        val found = searchSync(startNode, endNode, heuristic)
        return if (found) generateNodePath(startNode) else null
    }

    private fun searchSync(startNode: TNode, endNode: TNode, heuristic: IHeuristic<TNode>): Boolean {
        if (!graph.isValidEndNode(endNode)) {
            return false
        }

        initSearch(startNode, endNode, heuristic)

        // Iterate through processing each node
        do {
            // Retrieve the node with smallest estimated total cost from the open list
            val current = openList.pop()
            this.current = current
            current.status = NodeStatus.Closed

            // Terminate if we reached the goal node
            if (current.node == endNode) {
                return true
            }

            visitChildren(endNode, heuristic)
        } while (openList.size > 0)

        // We've run out of nodes without finding the goal, so there's no solution
        return false
    }

    private fun initSearch(startNode: TNode, endNode: TNode, heuristic: IHeuristic<TNode>) {
        metrics?.reset()

        // Increment the search id
        if (++searchId < 0) {
            searchId = 1
        }

        // Initialize the open list
        openList.clear()

        // Initialize the record for the start node and add it to the open list
        val startRecord = getNodeRecord(startNode)
        startRecord.node = startNode
        startRecord.connection = null
        startRecord.costSoFar = 0.0f
        addToOpenList(startRecord, heuristic.estimate(startNode, endNode))

        current = null
    }

    private fun visitChildren(endNode: TNode, heuristic: IHeuristic<TNode>) {
        // Get current node's outgoing connections
        val connections = graph.getConnections(graph, current!!.node!!)

        // Loop through each connection in turn
        for (connection in connections) {
            metrics?.let {
                it.visitedNodes++
            }

            // Get the cost estimate for the node
            val node = connection.toNode
            val nodeCost = current!!.costSoFar + connection.cost
            if (nodeCost > costCutoff) {
                logger.debug("Reached cost cutoff, skipping connection")
                continue
            }

            val nodeRecord = getNodeRecord(node)
            val nodeHeuristic = getNodeHeuristic(node, nodeRecord, nodeCost, endNode, heuristic) ?: continue

            if (nodeRecord.status == NodeStatus.Open) {
                openList.remove(nodeRecord)
            }

            // Update node record's cost and connection
            nodeRecord.costSoFar = nodeCost
            nodeRecord.connection = connection

            // Add it to the open list with the estimated total cost
            addToOpenList(nodeRecord, nodeCost + nodeHeuristic)
        }
    }

    private fun getNodeHeuristic(
        node: TNode,
        nodeRecord: NodeRecord<TNode>,
        nodeCost: Float,
        endNode: TNode,
        heuristic: IHeuristic<TNode>
    ): Float? {
        // The node is closed
        // -> If we didn't find a shorter route, skip
        // The node is open
        // -> If our route is no better, then skip
        // -> Remove it from the open list (it will be re-added with the new cost)

        if (nodeRecord.status == NodeStatus.Closed || nodeRecord.status == NodeStatus.Open) {
            if (nodeRecord.costSoFar <= nodeCost) {
                return null
            }

            // We can use the node's old cost values to calculate its heuristic
            // without calling the possibly expensive heuristic function
            return nodeRecord.estimatedTotalCost - nodeRecord.costSoFar
        }

        return heuristic.estimate(node, endNode)
    }

    private fun generateNodePath(startNode: TNode): NodePath<TNode> {
        val path = NodePath<TNode>()

        // Work back along the path, accumulating nodes
        while (current!!.connection != null) {
            path.add(current!!.node!!)

            val nodeKey = graph.getNodeKey(current!!.connection!!.fromNode)
            current = nodeRecords[nodeKey]
        }

        path.add(startNode)

        // Reverse the path
        path.reverse()

        return path
    }

    private fun addToOpenList(nodeRecord: NodeRecord<TNode>, estimatedTotalCost: Float) {
        openList.add(nodeRecord, estimatedTotalCost)
        nodeRecord.status = NodeStatus.Open
        metrics?.let { metrics ->
            metrics.openListAdditions++
            metrics.openListPeak = metrics.openListPeak.coerceAtLeast(openList.size)
        }
    }

    private fun getNodeRecord(node: TNode): NodeRecord<TNode> {
        val index = graph.getNodeKey(node)
        var record: NodeRecord<TNode>? = nodeRecords[index]
        if (record != null) {
            if (record.searchId != searchId) {
                record.status = NodeStatus.Unvisited
                record.searchId = searchId
            }
        } else {
            record = NodeRecord()
            record.node = node
            record.searchId = searchId
            nodeRecords[index] = record
        }
        return record
    }

}
