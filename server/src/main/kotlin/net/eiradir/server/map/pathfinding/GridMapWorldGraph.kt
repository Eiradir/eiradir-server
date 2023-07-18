package net.eiradir.server.map.pathfinding

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.pathfinding.INodeGraph
import net.eiradir.server.pathfinding.CombinedNodeGraph
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.view.MapView

class GridMapWorldGraph(private val mapView: MapView) : CombinedNodeGraph<Vector3Int, GridMapNode>() {

    private val graphCache = mutableMapOf<ChunkDimensions, GridMapGraph?>()

    override fun getGraphAt(position: Vector3): INodeGraph<Vector3Int, GridMapNode>? {
        val chunkPos = mapView.dimensions.of(position)
        return graphCache.computeIfAbsent(chunkPos) { key ->
            if(mapView.hasChunkAt(key)) {
                GridMapGraph(mapView, key)
            } else null
        }
    }

    override fun getNodePosition(node: GridMapNode): Vector3 {
        return node.position
    }

    override fun getNodeKey(node: GridMapNode): Vector3Int {
        return node.position.floorToIntVector()
    }
}