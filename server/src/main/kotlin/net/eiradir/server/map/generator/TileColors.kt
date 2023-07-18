package net.eiradir.server.map.generator

import com.google.common.collect.HashBiMap
import java.awt.Color

object TileColors {

    private val colorToTile = HashBiMap.create<Color, Byte>()
    private val tileToColor = colorToTile.inverse()

    private fun mapColor(hex: Int, id: Int) {
        colorToTile[Color(hex)] = id.toByte()
    }

    fun getColor(tileId: Byte): Color {
        return tileToColor[tileId] ?: Color.PINK
    }

    fun getTileId(color: Color): Byte? {
        return colorToTile[color]
    }

    init {
        mapColor(0x000000, 0) // invalid
        mapColor(0x99d9ea, 1) // water
        mapColor(0x13a213, 2) // Grass
        mapColor(0xcdcdcd, 3) // Tundra
        mapColor(0xc3c3c3, 4) // Tundra
        mapColor(0x3c6241, 5) // Forest Ground
        mapColor(0xe5e5e5, 6) // Snow
        mapColor(0x177935, 7) // Forest Taiga North
        mapColor(0x3f6749, 8) // Forest Caeril
        mapColor(0x2f6f5e, 9) // Forest Caeril
        mapColor(0x16c016, 10) // Meadows
        mapColor(0x86ac13, 11) // Swamp
        mapColor(0x6b8341, 12) // Swamp Darkon
        mapColor(0x696d38, 13) // Swamp Darkon
        mapColor(0x7b9629, 14) // Swamp Hemara
        mapColor(0x6c7629, 15) // Swamp Hemara
        mapColor(0x8caa46, 16) // Rocky Grass
        mapColor(0x608d36, 17) // Swamp Wetland
        mapColor(0x31ce41, 18) // Meadows
        mapColor(0x09ac32, 19) // Meadows
        mapColor(0xb97a57, 20) // Swamp Mud
        mapColor(0x7f7f7f, 21) // Mountains
        mapColor(0xf9f97d, 22) // Sand North
        mapColor(0x786047, 23) // Dirt Caeril
        mapColor(0xefe4b0, 24) // Sand South
        mapColor(0x9aab8b, 25) // Dry Grass
        mapColor(0xb5e61d, 26) // Savannah
        mapColor(0xd9e91b, 27) // Savannah
        mapColor(0xdeec35, 28) // Savannah
        mapColor(0xefd832, 29) // Savannah
        mapColor(0x599161, 30) // Steppe
        mapColor(0x86a87d, 31) // Steppe
        mapColor(0x22b14c, 32) // Grass
        mapColor(0x1fa346, 33) // Grass
        mapColor(0x15bd15, 34) // Lawn
        mapColor(0x167c51, 35) // Forest Darkon
        mapColor(0x1d9441, 36) // Forest Hemara
        mapColor(0x745f3f, 37) // Red Forest Caeril
        mapColor(0xd9822b, 38) // Red Forest Caeril
        mapColor(0xc1782f, 39) // Red Forest Caeril
        mapColor(0x86572d, 40) // Red Forest Caeril
        mapColor(0xe2b587, 41) // Red Forest Caeril
        mapColor(0xf7be2b, 42) // Red Forest Caeril
        mapColor(0x209f46, 43) // Grass Darkon
        mapColor(0x7d8d74, 44) // Gravel
        mapColor(0x7a9d11, 45) // Marsh
        mapColor(0x2fd084, 46) // Meadows Darkon
        mapColor(0x5f7939, 47) // Mire
        mapColor(0x226c3e, 48) // Moor Darkon
        mapColor(0x579a1b, 49) // Moor
        mapColor(0x4d8817, 50) // Moor
        mapColor(0x177b35, 51) // Moor
        mapColor(0x78b458, 52) // Moor
        mapColor(0x56863c, 53) // Moor
        mapColor(0x5c4936, 54) // Mud
        mapColor(0x9fa778, 55) // Pebbles
        mapColor(0xa7ae84, 56) // Pebbles
        mapColor(0x867d71, 57) // Rocks
        mapColor(0x88736f, 58) // Stone
        mapColor(0x7a6d38, 59) // Stone
        mapColor(0x675534, 60) // Stone // used to be 0x86432d but wasn't used anywhere, whereas 0x675534 wasn't mapped, Kon has no brain
        mapColor(0x3c5977, 61) // Stone
        mapColor(0x60939d, 62) // Stone
        mapColor(0x97afb3, 63) // Stone
        mapColor(0xb3aa97, 64) // Stone
        mapColor(0x98a693, 65) // Stone
        mapColor(0x7c592e, 66) // Dirt
        mapColor(0x46714d, 67) // Forest Caeril
    }
}