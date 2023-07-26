package net.eiradir.server.trait.data

import net.eiradir.server.stats.Buff

class TraitBuilder(val name: String) {
    var category: String = "trait"
    var valence: Trait.Valence = Trait.Valence.Neutral
    var availableAtCreation: Boolean = false
    var stackBuffs: Boolean = false
    private val providesTraits: MutableSet<String> = mutableSetOf()
    private val providesBuffs: MutableList<Pair<String, Buff>> = mutableListOf()

    fun provide(trait: String): TraitBuilder {
        providesTraits.add(trait)
        return this
    }

    fun buff(stat: String, buff: Buff): TraitBuilder {
        providesBuffs.add(stat to buff)
        return this
    }

    fun build(): Trait {
        return Trait(
            name = name,
            category = category,
            valence = valence,
            providesTraits = providesTraits,
            providesBuffs = providesBuffs,
            availableAtCreation = availableAtCreation,
            stackBuffs = stackBuffs,
        )
    }
}