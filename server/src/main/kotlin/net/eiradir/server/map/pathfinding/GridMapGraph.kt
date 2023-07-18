package net.eiradir.server.map.pathfinding

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.extensions.offset
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.pathfinding.*
import net.eiradir.server.map.tilemap.MapChunk
import net.eiradir.server.map.view.MapView
import java.util.stream.Stream
import kotlin.math.max

class GridMapGraph(private val mapView: MapView, private val chunkPos: ChunkDimensions) : INodeGraph<Vector3Int, GridMapNode> {
    override fun getConnections(graph: INodeGraph<Vector3Int, GridMapNode>, fromNode: GridMapNode): Stream<IConnection<GridMapNode>> {
        val connections = mutableListOf<IConnection<GridMapNode>>()
        for (direction in GridDirection.horizontalValues) {
            val isBlocked = BlockedGridDirection.isBlocked(fromNode.blockedDirections, direction)
            if (isBlocked) {
                continue
            }

            val otherNode = graph.getNodeAt(fromNode.position.offset(direction), NodeSearchMode.NearestWalkable)
            if (otherNode != null) {
                val isOtherNotWalkable = !otherNode.isWalkable
                if (isOtherNotWalkable) {
                    continue
                }

                val isBlockedFromOther = BlockedGridDirection.isBlocked(otherNode.blockedDirections, direction.opposite)
                if (isBlockedFromOther) {
                    continue
                }

                val upwardsDistance = max(0f, otherNode.position.y - fromNode.position.y)
                val isOtherTooFarUp = upwardsDistance >= 2f
                if (isOtherTooFarUp) {
                    continue
                }

                val cost = direction.cost
                connections.add(DefaultConnection(fromNode, otherNode, cost))
            }
        }
        return connections.stream()
    }

    override fun getNodeKey(node: GridMapNode): Vector3Int {
        // Usually not used since the implementation in EiradirWorldGraph is used instead
        return node.position.floorToIntVector()
    }

    override fun getNodeAt(position: Vector3, searchMode: NodeSearchMode): GridMapNode? {
        val intPosition = position.floorToIntVector()
        val tile = mapView.getTileAt(intPosition) ?: return null
        return GridMapNode(intPosition.toVector3().add(0.5f, 0.5f, 0f))
    }

    override fun isValidEndNode(node: GridMapNode): Boolean {
        return node.isWalkable
    }
}