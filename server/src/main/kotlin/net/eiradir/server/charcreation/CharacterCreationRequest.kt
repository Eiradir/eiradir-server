package net.eiradir.server.charcreation

import net.eiradir.server.player.CharacterSex

data class CharacterCreationRequestGear(val id: Int, val color: Int)
data class CharacterCreationRequestVisualTrait(val id: Int, val color: Int)

data class CharacterCreationRequest(
    val name: String,
    val raceId: Int,
    val sex: CharacterSex,
    val age: Int,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val agility: Int,
    val perception: Int,
    val intelligence: Int,
    val arcanum: Int,
    val traits: List<Int>,
    val skinColor: Int,
    val gear: List<CharacterCreationRequestGear>,
    val visualTraits: List<CharacterCreationRequestVisualTrait>
)