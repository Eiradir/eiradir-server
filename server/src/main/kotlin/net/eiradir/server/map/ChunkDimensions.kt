package net.eiradir.server.map

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import kotlin.math.abs
import kotlin.math.sqrt

data class ChunkDimensions(val x: Int, val y: Int, val level: Int, val size: Int) {
    fun toRelativeX(x: Int): Int {
        return x - this.x * size
    }

    fun toRelativeLevel(level: Int): Int {
        return level - this.level * 3
    }

    fun toRelativeY(y: Int): Int {
        return y - this.y * size
    }

    fun toAbsoluteX(x: Int): Int {
        return x + this.x * size
    }

    fun toAbsoluteLevel(level: Int): Int {
        return level + this.level * 3
    }

    fun toAbsoluteY(y: Int): Int {
        return y + this.y * size
    }

    fun offset(offsetX: Int, offsetY: Int, offsetLevel: Int): ChunkDimensions {
        return ChunkDimensions(x + offsetX, y + offsetY, level + offsetLevel, size)
    }

    fun offset(dir: GridDirection): ChunkDimensions {
        return ChunkDimensions(x + dir.offsetX, y + dir.offsetY, level + dir.offsetLevel, size)
    }

    fun of(x: Int, y: Int, level: Int): ChunkDimensions {
        return ChunkDimensions(x, y, level, size)
    }

    fun of(position: Vector3): ChunkDimensions {
        return of(
            MathUtils.floor(position.x / size.toFloat()),
            MathUtils.floor(position.y / size.toFloat()),
            MathUtils.floor(position.z / 3f)
        )
    }

    fun of(position: Vector3Int): ChunkDimensions {
        return of(
            MathUtils.floor(position.x / size.toFloat()),
            MathUtils.floor(position.y / size.toFloat()),
            MathUtils.floor(position.level / 3f)
        )
    }

    fun getHorizontalDistance(chunkPos: ChunkDimensions): Float {
        val dx = (chunkPos.x - x).toFloat()
        val dy = (chunkPos.y - y).toFloat()
        return sqrt(dx * dx + dy * dy)
    }

    fun getDistanceManhattan(chunkPos: ChunkDimensions): Int {
        val dx = abs(chunkPos.x - x)
        val dy = abs(chunkPos.y - y)
        val dl = abs(chunkPos.level - level)
        return dx + dy + dl
    }
}