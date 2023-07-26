package net.eiradir.server.charcreation

import net.eiradir.server.trait.data.Trait

data class CharacterCreationTrait(val id: Int, val name: String, val description: String, val category: String, val valence: Trait.Valence, val provides: List<Int>)
