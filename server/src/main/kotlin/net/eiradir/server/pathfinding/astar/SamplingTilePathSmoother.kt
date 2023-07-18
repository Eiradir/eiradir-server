package net.eiradir.server.pathfinding.astar

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.extensions.directionTo
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.pathfinding.BlockedGridDirection
import net.eiradir.server.pathfinding.INodeGraph
import net.eiradir.server.pathfinding.NodeSearchMode
import net.eiradir.server.map.pathfinding.GridMapNode
import kotlin.math.ceil


class SamplingTilePathSmoother(
    private val graph: INodeGraph<Vector3Int, GridMapNode>,
    private val limitDirections: Boolean = false,
    private val gridAlignLast: Boolean = true
) : IPathSmoother<GridMapNode, GridMapNode> {

    override fun smoothPathSync(path: NodePath<GridMapNode>): NodePath<GridMapNode> {
        val smoothedPath = NodePath<GridMapNode>()
        val end = path[path.size - 1]

        for (node in path) {
            val sampledNodes = sampleSmoothLine(node.position, end.position)
            if (sampledNodes != null) {
                smoothedPath.addAll(sampledNodes)
            }

            smoothedPath.add(node)
        }

        return smoothedPath
    }

    private fun sampleSmoothLine(startPos: Vector3, endPos: Vector3): NodePath<GridMapNode>? {
        val result = NodePath<GridMapNode>()
        val distance = startPos.dst(endPos)
        val directionVec = endPos.cpy().sub(startPos).nor()
        var lastNode: GridMapNode? = null
        val totalSteps = ceil(distance).toInt()
        for (step in 0..totalSteps) {
            val pos = startPos.cpy().add(directionVec.x * step, directionVec.y * step, directionVec.z * step)
            val node = graph.getNodeAt(pos, NodeSearchMode.Nearest)
            if (node != null) {
                val isWalkable = node.isWalkable
                if (!isWalkable) {
                    return null
                }

                if (lastNode != null) {
                    val direction = lastNode.position.directionTo(node.position)
                    val isBlockedFromLast = BlockedGridDirection.isBlocked(lastNode.blockedDirections, direction)
                    if (isBlockedFromLast) {
                        return null
                    }

                    val isBlockedToLast = BlockedGridDirection.isBlocked(node.blockedDirections, direction.opposite)
                    if (isBlockedToLast) {
                        return null
                    }
                }

                var smoothSampledPos = Vector3(pos.x, pos.y, node.position.z)
                if (step == totalSteps && gridAlignLast) {
                    smoothSampledPos = smoothSampledPos.floorToIntVector().toVector3().add(0.5f, 0.5f, 0f)
                }

                // If sampled node is on same grid position as this node, we adjust the last instead of adding a new node to the path
                val lastSampledNode = result.getOrNull(result.size - 1)
                val sampledNode = if (limitDirections) node else GridMapNode(Vector3(pos.x, pos.y, node.position.z), node.blockedDirections)
                if (lastSampledNode?.position?.floorToIntVector() == smoothSampledPos.floorToIntVector()) {
                    result[result.size - 1] = sampledNode
                } else {
                    result.add(sampledNode)
                }

                lastNode = node
            } else {
                return null
            }
        }
        return result
    }
}