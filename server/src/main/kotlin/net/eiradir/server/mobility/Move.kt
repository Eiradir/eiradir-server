package net.eiradir.server.mobility

import net.eiradir.server.math.Vector3Int

data class Move(val targetPosition: Vector3Int, val duration: Float) {
    var timePassed = 0f
}