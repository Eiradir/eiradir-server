package net.eiradir.server.nature

import net.eiradir.server.data.Tile

object NatureBiomes {

    enum class NatureType {
        Bush,
        Flower,
        Grass,
        Mushroom,
        MushroomCircle,
        Fern,
        Rock,
        Tree,
        Sunflower,
        Weed,
        CaveWall;

        fun supports(tile: Tile): Boolean {
            if (this == CaveWall) {
                return tile.name == "rocks" || tile.name.startsWith("stone_") || tile.name.startsWith("mountains")
            }
            return tile.name != "water"
        }
    }

    class BiomeData {
        val items = mutableMapOf<NatureType, List<String>>()
        val probabilities = mutableMapOf<NatureType, Float>()

        fun set(type: NatureType, probability: Float, items: List<String>): BiomeData {
            this.items[type] = items
            this.probabilities[type] = probability
            return this
        }
    }

    val caeril = BiomeData()
        .set(
            NatureType.Bush, 1f, NatureItems.bushes + listOf(
                "caeril_bush_02",
                "caeril_bush_03"
            )
        )
        .set(
            NatureType.Fern, 1f, listOf(
                "caeril_fern_01",
                "caeril_fern_02",
                "caeril_fern_03",
                "caeril_fern_04",
                "caeril_fern_05",
                "caeril_fern_06",
                "darkon_fern_01",
                "darkon_fern_02",
                "darkon_fern_03",
                "darkon_fern_04",
                "darkon_fern_05",
                "darkon_fern_06"
            )
        )

    val darkon = BiomeData()
        .set(
            NatureType.Bush, 1f,
            NatureItems.bushes + listOf(
                "darkon_bush_01",
                "darkon_bush_02",
                "darkon_bush_03",
            )
        )
        .set(
            NatureType.Fern, 1f, listOf(
                "darkon_fern_01",
                "darkon_fern_02",
                "darkon_fern_03",
                "darkon_fern_04",
                "darkon_fern_05",
                "darkon_fern_06"
            )
        )

    val hemara = BiomeData()
        .set(
            NatureType.Bush, 1f,
            NatureItems.bushes + listOf(
                "hemara_bush_01",
                "hemara_bush_02",
                "hemara_bush_03",
            )
        )

    val invariDesert = BiomeData()
    val whiteDesert = BiomeData()
    val orilis = BiomeData()

    val maldor = BiomeData()
        .set(NatureType.Bush, 0.01f, NatureItems.bushes + NatureItems.fruitBushes)
        .set(NatureType.Flower, 0.01f, NatureItems.flowers)
        .set(NatureType.Grass, 0.01f, NatureItems.grass)
        .set(NatureType.Fern, 0.01f, NatureItems.ferns)
        .set(NatureType.Rock, 0.01f, NatureItems.tinyRocks)
        .set(NatureType.Tree, 0.01f, NatureItems.trees)
        .set(NatureType.Sunflower, 0.001f, NatureItems.sunflowers)
        .set(NatureType.Weed, 0.01f, NatureItems.weeds)
        .set(NatureType.CaveWall, 1f, NatureItems.caveWalls)

}