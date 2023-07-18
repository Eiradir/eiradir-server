package net.eiradir.server.entity

import net.eiradir.server.entity.network.NetworkedDataKey
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import java.util.UUID

class NetworkedEntity(
    val uniqueId: UUID,
    val position: Vector3Int,
    val direction: GridDirection,
    val isoId: Int
) {
    val data = mutableMapOf<NetworkedDataKey, Any?>()

    override fun toString(): String {
        return "NetworkedEntity(uniqueId=$uniqueId, position=$position, direction=$direction, isoId=$isoId, data=$data)"
    }
}