package net.eiradir.server.data.builder

import net.eiradir.server.data.Tile
import net.eiradir.server.data.TransitionMode

class TileBuilder(
    val name: String
) {
    var transitionMode = TransitionMode.Normal

    fun build(): Tile {
        return Tile(name).also {
            it.transitionMode = transitionMode
        }
    }

}

