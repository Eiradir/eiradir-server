package net.eiradir.server.extensions

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import kotlin.math.abs
import kotlin.math.floor

fun Vector3.floorToIntVector(): Vector3Int {
    return Vector3Int(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
}

fun Vector3.offset(dir: GridDirection, n: Int = 1): Vector3 {
    if (n == 0) {
        return this
    }

    return Vector3(x + dir.offsetX * n, y + dir.offsetY * n, z + dir.offsetLevel * n)
}

fun Vector3.center(): Vector3 {
    x = floor(x) + 0.5f
    y = floor(y) + 0.5f
    return this
}

fun Vector3.directionTo(position: Vector3): GridDirection {
    var dirX = position.x - x
    var dirY = position.y - y
    var dirLevel = position.z - z
    if(dirX != 0f) {
        dirX /= abs(dirX)
    }
    if(dirY != 0f) {
        dirY /= abs(dirY)
    }
    if(dirLevel != 0f) {
        dirLevel /= abs(dirLevel)
    }

    return GridDirection.fromOffset(dirX.toInt(), dirY.toInt(), dirLevel.toInt())
}