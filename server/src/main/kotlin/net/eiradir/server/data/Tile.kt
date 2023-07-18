package net.eiradir.server.data

import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Tile(override val name: String) : RegistryEntry<Tile> {

    var transitionMode = TransitionMode.Normal
    var transitionDominance = 0

    fun isTransitionDominant(other: Tile): Boolean {
        return transitionMode != TransitionMode.CannotTransition && transitionDominance > other.transitionDominance
    }

    override fun registry(registries: Registries): Registry<Tile> {
        return registries.tiles
    }

    companion object {
        val Invalid = Tile("invalid")
        val Clear = Tile("clear")
    }
}