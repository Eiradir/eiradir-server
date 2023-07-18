package net.eiradir.server.camera

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.network.ConnectionInitializer

class CameraComponent : Component, ConnectionInitializer {
    var position: Vector3Int = Vector3Int.Zero
    var lastPosition: Vector3Int = Vector3Int(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    var followEntity: Entity? = null
    val watchedChunks = mutableSetOf<ChunkDimensions>()

    override val isReady get() = watchedChunks.isNotEmpty()
}