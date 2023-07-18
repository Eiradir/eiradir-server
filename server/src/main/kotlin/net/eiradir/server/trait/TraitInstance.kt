package net.eiradir.server.trait

import net.eiradir.server.stats.buff.BuffInstance

class TraitInstance(val trait: Trait, val data: Map<String, String>, val transient: Boolean) {
    val providedBuffs = mutableListOf<BuffInstance>()
    val providedTraits = mutableListOf<TraitInstance>()
}