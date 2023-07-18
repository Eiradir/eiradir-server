package net.eiradir.server.data.builder

import net.eiradir.server.registry.Registries

class TransitionsBuilder(private val registries: Registries) {

    private val definitions = mutableListOf<TileTransitionBuilder>()

    data class TileTransitionBuilder(val name: String) {
        var onto: String? = null

        fun transitionsOnto(name: String) {
            onto = name
        }
    }

    fun tile(name: String): TileTransitionBuilder {
        return TileTransitionBuilder(name).also { definitions.add(it) }
    }

    fun build() {
        val tiles = registries.tiles.getAll().associateBy { it.name }
        val result = tiles.values.asSequence().sortedBy { it.transitionMode }.toMutableList()
        for (definition in definitions) {
            val tile = tiles[definition.name] ?: continue
            val onto = tiles[definition.onto] ?: continue
            result.remove(tile)
            result.add(result.indexOf(onto) + 1, tile)
        }
        for ((index, tile) in result.withIndex()) {
            tile.transitionDominance = index
        }
    }
}