package net.eiradir.server.charcreation

import net.eiradir.server.registry.IdResolver
import net.eiradir.server.registry.Registry

class PlayableRaceRegistry(idResolver: IdResolver): Registry<PlayableRace>("playable_races", idResolver) {
    override fun invalid(name: String): PlayableRace {
        return PlayableRace(name)
    }
}