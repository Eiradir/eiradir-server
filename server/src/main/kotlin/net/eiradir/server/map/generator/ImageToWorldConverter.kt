package net.eiradir.server.map.generator

import net.eiradir.server.map.tilemap.PersistentChunkedMap
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main() {
    ImageToWorldConverter.run(File("content/worldmap-x1-padded.png"), File("maps"), "base")
}

object ImageToWorldConverter {

    fun run(worldImageFile: File, mapsDirectory: File, name: String) {
        val input = ImageIO.read(worldImageFile)
        val registries = GeneratorUtils.defaultRegistries()
        val writeStorage = PersistentChunkedMap(registries, mapsDirectory, name)
        val chunkSizeX = input.width / writeStorage.descriptor.size
        val chunkSizeY = input.height / writeStorage.descriptor.size
        for (chunkX in 0 until chunkSizeX) {
            println("Progress: $chunkX/$chunkSizeX")
            for (chunkY in 0 until chunkSizeY) {
                val descriptor = writeStorage.descriptor.of(chunkX.toShort().toInt(), chunkY.toShort().toInt(), 0)
                val chunk = writeStorage.getOrCreateChunkAt(descriptor)
                val tileMap = ByteArray(descriptor.size * descriptor.size)
                val rgb = IntArray(descriptor.size * descriptor.size)
                input.getRGB(chunkX * descriptor.size, chunkY * descriptor.size, descriptor.size, descriptor.size, rgb, 0, descriptor.size)
                for (y in 0 until descriptor.size) {
                    for (x in 0 until descriptor.size) {
                        val tileId = TileColors.getTileId(Color(rgb[x + y * descriptor.size]))
                        if (tileId != null) {
                            tileMap[y * descriptor.size + x] = tileId
                        } else {
                            System.err.printf(
                                "Invalid color %s at (%d, %d) - no tile mapping!\n",
                                Color(rgb[x + y * descriptor.size]),
                                chunkX * descriptor.size + x,
                                chunkY * descriptor.size + y
                            )
                        }
                    }
                }
                chunk.replaceBackingArray(tileMap)
                chunk.markDirty()
            }
        }

        writeStorage.saveAllChanged()

        ImageExporter.run(mapsDirectory, name, File(mapsDirectory, "$name-export"))
    }
}