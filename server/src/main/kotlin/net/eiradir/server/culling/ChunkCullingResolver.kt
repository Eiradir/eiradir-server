package net.eiradir.server.culling

import net.eiradir.server.map.ChunkDimensions
import kotlin.math.floor

class ChunkCullingResolver {

    data class CullingResult(val toWatch: Set<ChunkDimensions>, val toUnwatch: Set<ChunkDimensions>)

    fun updateWatchesAround(
        watchedChunks: Set<ChunkDimensions>,
        chunkPos: ChunkDimensions,
        cullRange: Int,
        verticalCullRange: Int,
        isInsideBuilding: Boolean
    ): CullingResult {
        val toWatch = mutableSetOf<ChunkDimensions>()
        val toUnwatch = mutableSetOf<ChunkDimensions>()
        for (x in -cullRange..cullRange) {
            for (y in -cullRange..cullRange) {
                for (level in -verticalCullRange..verticalCullRange) {
                    val offsetChunkPos = chunkPos.offset(x, y, level)
                    if (isWithinCullRange(
                            chunkPos,
                            offsetChunkPos,
                            cullRange,
                            verticalCullRange,
                            isInsideBuilding
                        ) && !watchedChunks.contains(offsetChunkPos)
                    ) {
                        toWatch.add(offsetChunkPos)
                    }
                }
            }
        }

        for (watchedChunk in watchedChunks) {
            if (!isWithinCullRange(chunkPos, watchedChunk, cullRange, verticalCullRange, isInsideBuilding)) {
                toUnwatch.add(watchedChunk)
            }
        }

        return CullingResult(toWatch, toUnwatch)
    }

    fun isWithinCullRange(
        sourceChunkPos: ChunkDimensions,
        watchedChunkPos: ChunkDimensions,
        cullRange: Int,
        verticalCullRange: Int,
        isInsideBuilding: Boolean
    ): Boolean {
        val currentY = sourceChunkPos.level
        if (currentY >= 0 && watchedChunkPos.level < 0) {
            return false
        } else if (currentY < 0 && watchedChunkPos.level != currentY) {
            return false
        } else if (isInsideBuilding && watchedChunkPos.level > currentY) {
            return false
        }

        val minY = currentY - verticalCullRange
        val maxY = currentY + verticalCullRange
        val distance = watchedChunkPos.getHorizontalDistance(sourceChunkPos)
        return floor(distance) <= cullRange && currentY in minY..maxY
    }
}