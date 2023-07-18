package net.eiradir.server.math

import com.badlogic.gdx.math.Vector3
import kotlin.math.abs
import kotlin.math.sqrt

data class Vector3Int(val x: Int, val y: Int, val level: Int) {
    companion object {
        val Zero = Vector3Int(0, 0, 0)
        val One = Vector3Int(1, 1, 1)
    }

    fun toVector3(): Vector3 {
        return Vector3(x.toFloat(), y.toFloat(), level.toFloat())
    }

    fun toCenteredVector3(): Vector3 {
        return Vector3(x.toFloat() + 0.5f, y.toFloat() + 0.5f, level.toFloat())
    }

    fun offset(dir: GridDirection, n: Int = 1): Vector3Int {
        if (n == 0) {
            return this
        }

        return Vector3Int(x + dir.offsetX * n, y + dir.offsetY * n, level + dir.offsetLevel * n)
    }

    fun dst(vector: Vector3Int): Float {
        val xd = vector.x - x
        val yd = vector.y - y
        val ld = vector.level - level
        return sqrt((xd * xd + yd * yd + ld * ld).toDouble()).toFloat()
    }

    fun dst2(vector: Vector3Int): Int {
        val xd = vector.x - x
        val yd = vector.y - y
        val ld = vector.level - level
        return xd * xd + yd * yd + ld * ld
    }

    fun directionTo(position: Vector3Int): GridDirection {
        var dirX = position.x - x
        var dirY = position.y - y
        var dirLevel = position.level - level
        if(dirX != 0) {
            dirX /= abs(dirX)
        }
        if(dirY != 0) {
            dirY /= abs(dirY)
        }
        if(dirLevel != 0) {
            dirLevel /= abs(dirLevel)
        }

        return GridDirection.fromOffset(dirX, dirY, dirLevel)
    }

}
