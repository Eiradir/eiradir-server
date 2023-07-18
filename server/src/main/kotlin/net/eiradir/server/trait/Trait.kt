package net.eiradir.server.trait

import net.eiradir.server.data.IconType
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import net.eiradir.server.stats.buff.Buff

data class Trait(
    override val name: String,
    val iconName: String = name,
    val category: String = "trait",
    var valence: Valence = Valence.Neutral,
    val availableAtCreation: Boolean = false,
    val providesTraits: Set<String> = emptySet(),
    val providesBuffs: List<Pair<String, Buff>> = emptyList(),
    val stackBuffs: Boolean = false,
) : RegistryEntry<Trait>, IconType {
    enum class Valence {
        Neutral, Good, Bad,
    }

    override fun registry(registries: Registries): Registry<Trait> {
        return registries.traits
    }

    override fun iconId(registries: Registries): Int {
        return registries.idResolver.resolve("icons", iconName) ?: 0
    }
}