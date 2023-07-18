package net.eiradir.server.math

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

enum class GridDirection(val offsetX: Int, val offsetLevel: Int, val offsetY: Int, val angle: Float, val mask: Int) {
    None(0, 0, 0, 0f, 0),
    SouthEast(1, 0, -1, 90f + 45f, 8),
    South(0, 0, -1, 180f, 16),
    SouthWest(-1, 0, -1, 180f + 45f, 32),
    West(-1, 0, 0, 270f, 64),
    NorthWest(-1, 0, 1, 270f + 45f, 128),
    North(0, 0, 1, 0f, 1),
    NorthEast(1, 0, 1, 45f, 2),
    East(1, 0, 0, 90f, 4),
    Up(0, 3, 0, 0f, 0),
    Down(0, -3, 0, 0f, 0);

    val isDiagonal = offsetX != 0 && offsetY != 0

    val opposite: GridDirection by lazy {
        when (this) {
            North -> South
            NorthEast -> SouthWest
            East -> West
            SouthEast -> NorthWest
            South -> North
            SouthWest -> NorthEast
            West -> East
            NorthWest -> SouthEast
            Up -> Down
            Down -> Up
            else -> None
        }
    }

    val cost: Float by lazy {
        when (this) {
            North -> 1f
            East -> 1f
            South -> 1f
            West -> 1f
            NorthEast -> sqrt(2f)
            SouthEast -> sqrt(2f)
            SouthWest -> sqrt(2f)
            NorthWest -> sqrt(2f)
            else -> 0f
        }
    }

//    fun passesCollision(flags: Int): Boolean { TODO
//        if (flags == 0) {
//            return true
//        }
//        return when (this) {
//            NorthWest -> CollisionFlags.NorthWest and flags == 0
//            North -> CollisionFlags.North and flags == 0
//            NorthEast -> CollisionFlags.NorthEast and flags == 0
//            East -> CollisionFlags.East and flags == 0
//            SouthEast -> CollisionFlags.SouthEast and flags == 0
//            South -> CollisionFlags.South and flags == 0
//            SouthWest -> CollisionFlags.SouthWest and flags == 0
//            West -> CollisionFlags.West and flags == 0
//            else -> false
//        }
//    }

    companion object {
        private val values = values()
        val horizontalValues = listOf(SouthEast, South, SouthWest, East, West, NorthEast, North, NorthWest)
        val horizontalValuesManhattan = listOf(South, East, West, North)

        fun fromOffset(dirX: Int, dirY: Int, dirLevel: Int): GridDirection {
            return values.firstOrNull { it.offsetX == dirX && it.offsetY == dirY && it.offsetLevel == dirLevel } ?: None
        }

        fun fromId(id: Int): GridDirection {
            return values[id]
        }

        fun fromAngle(angle: Float): GridDirection {
            return fromAngle(angle, horizontalValues)
        }

        fun fromAngleManhattan(angle: Float): GridDirection {
            return fromAngle(angle, horizontalValuesManhattan)
        }

        private fun fromAngle(angle: Float, candidates: List<GridDirection>): GridDirection {
            val clampedAngle = floor(angle).toInt() % 360
            var minAngle = Float.POSITIVE_INFINITY
            var minDir = None
            for (dir in candidates) {
                val delta = abs(deltaAngle(dir.angle, clampedAngle.toFloat()))
                if (delta <= minAngle) {
                    minDir = dir
                    minAngle = delta
                }
            }

            return minDir
        }

        private fun deltaAngle(current: Float, target: Float): Float {
            var num = repeat(target - current, 360f)
            if (num.toDouble() > 180.0) num -= 360f
            return num
        }

        private fun repeat(t: Float, length: Float): Float {
            return clamp(t - floor(t / length) * length, 0.0f, length)
        }

        private fun clamp(value: Float, min: Float, max: Float): Float {
            var result = value
            if (result < min) result = min else if (result.toDouble() > max.toDouble()) result = max
            return result
        }
    }

    object Flags {
        const val None = 0
        private const val SouthEast = 1
        private const val South = 2
        private const val SouthWest = 4
        private const val East = 8
        private const val West = 16
        private const val NorthEast = 32
        private const val North = 64
        private const val NorthWest = 128
        const val Up = 256
        const val Down = 512
        const val All = SouthEast or South or SouthWest or East or West or NorthEast or North or NorthWest
    }
}
