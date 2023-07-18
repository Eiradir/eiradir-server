package net.eiradir.server.data.builder

import net.eiradir.server.data.Race
import net.eiradir.server.data.StatType
import net.eiradir.server.data.Tile
import net.eiradir.server.data.TransitionMode

class RaceBuilder(
    val name: String
) {

    fun build(): Race {
        return Race(name)
    }

}

