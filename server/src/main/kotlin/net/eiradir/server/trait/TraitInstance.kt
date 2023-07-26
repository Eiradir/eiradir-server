package net.eiradir.server.trait

import net.eiradir.server.stats.BuffInstance
import net.eiradir.server.trait.data.Trait

class TraitInstance(val trait: Trait, val data: Map<String, String>, val transient: Boolean) {
    val providedBuffs = mutableListOf<BuffInstance>()
    val providedTraits = mutableListOf<TraitInstance>()
}