package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.entity.network.NetworkedDataKey
import net.eiradir.server.extensions.CloneableComponent
import net.eiradir.server.io.SupportedInput
import net.eiradir.server.io.SupportedOutput
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int

data class GridTransform(var position: Vector3Int = Vector3Int.Zero, var direction: GridDirection = GridDirection.SouthEast) : PersistedComponent, CloneableComponent {
    var hasLastPosition = false
    var lastPosition = Vector3Int(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
    var lastDirection: GridDirection = GridDirection.SouthEast

    override val serializedName: String = "GridTransform"

    override fun save(buf: SupportedOutput) {
        buf.writeVector3Int(position)
    }

    override fun load(buf: SupportedInput) {
        position = buf.readVector3Int()
    }

    override fun copy(): Component {
        return GridTransform(position)
    }
}