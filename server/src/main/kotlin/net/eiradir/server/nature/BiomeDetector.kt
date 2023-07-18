package net.eiradir.server.nature

import net.eiradir.server.map.ChunkDimensions

class BiomeDetector {
    fun getBiomeAt(chunk: ChunkDimensions): NatureBiomes.BiomeData {
        return NatureBiomes.maldor
    }
}