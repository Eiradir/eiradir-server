package net.eiradir.server.map.generator

import net.eiradir.server.map.tilemap.PersistentChunkedMap
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageExporter {
    fun run(mapsDirectory: File, name: String, outputDirectory: File) {
        outputDirectory.mkdirs()

        val registries = GeneratorUtils.defaultRegistries()
        val readStorage = PersistentChunkedMap(registries, mapsDirectory, name)
        val outputs = mutableMapOf<Int, BufferedImage>()
        readStorage.loadAllChunks()
        val minX = readStorage.getLoadedChunks().minOf { it.dimensions.toAbsoluteX(0) }
        val minY = readStorage.getLoadedChunks().minOf { it.dimensions.toAbsoluteY(0) }
        val maxX = readStorage.getLoadedChunks().maxOf { it.dimensions.toAbsoluteX(it.dimensions.size) }
        val maxY = readStorage.getLoadedChunks().maxOf { it.dimensions.toAbsoluteY(it.dimensions.size) }
        val width = maxX - minX + 1
        val height = maxY - minY + 1
        for (chunk in readStorage.getLoadedChunks()) {
            val tileMap = chunk.getBackingArray()
            val output = outputs.getOrPut(chunk.dimensions.level) { BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB) }
            for (y in 0 until chunk.dimensions.size) {
                for (x in 0 until chunk.dimensions.size) {
                    val color = TileColors.getColor(tileMap[y * chunk.dimensions.size + x]).rgb
                    val absoluteX = chunk.dimensions.toAbsoluteX(x) - minX
                    val absoluteY = chunk.dimensions.toAbsoluteY(y) - minY
                    output.setRGB(absoluteX, absoluteY, color)
                }
            }
        }

        for (output in outputs) {
            ImageIO.write(output.value, "PNG", File(outputDirectory, "$name-${output.key}.png"))
        }
    }
}