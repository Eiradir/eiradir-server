package net.eiradir.server.persistence

import com.badlogic.ashley.core.Entity
import net.eiradir.server.player.GameCharacter

interface CharacterStorage {
    fun getCharactersByAccountId(accountId: String): List<GameCharacter>
    fun loadCharacterById(characterId: Int): GameCharacter?
    fun save(character: GameCharacter): Int
    fun isLocked(characterId: Int): Boolean
    fun lock(characterId: Int)
    fun unlock(characterId: Int)
    fun persist(entity: Entity): GameCharacter
}