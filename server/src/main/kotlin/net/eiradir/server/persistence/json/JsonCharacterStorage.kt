package net.eiradir.server.persistence.json

import com.badlogic.ashley.core.Entity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.*
import net.eiradir.server.extensions.toIntRGBA
import net.eiradir.server.math.GridDirection
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.persistence.CharacterStorage
import net.eiradir.server.persistence.DatabasePersistenceComponent
import net.eiradir.server.player.GameCharacter
import net.eiradir.server.player.TraitInstanceData
import net.eiradir.server.process.entity.ProcessComponent
import net.eiradir.server.process.persistence.ProcessInstanceData
import net.eiradir.server.registry.Registries
import net.eiradir.server.stats.entity.StatsComponent
import net.eiradir.server.trait.entity.TraitsComponent
import java.io.File

class JsonCharacterStorage(private val registries: Registries) : CharacterStorage {

    private val persistenceMapper = mapperFor<DatabasePersistenceComponent>()
    private val nameMapper = mapperFor<NameComponent>()
    private val transformMapper = mapperFor<GridTransform>()
    private val raceMapper = mapperFor<RaceComponent>()
    private val colorMapper = mapperFor<ColorComponent>()
    private val visualTraitsMapper = mapperFor<VisualTraitsComponent>()
    private val statsMapper = mapperFor<StatsComponent>()
    private val inventoryMapper = mapperFor<InventoryComponent>()
    private val processMapper = mapperFor<ProcessComponent>()
    private val traitsMapper = mapperFor<TraitsComponent>()

    private val baseDir = File("jdata")
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val locks = mutableSetOf<Int>()

    override fun getCharactersByAccountId(accountId: String): List<GameCharacter> {
        val accountFile = File(baseDir, "accounts/$accountId.json")
        if (accountFile.exists()) {
            val account = objectMapper.readValue(accountFile, JsonAccount::class.java)
            return account.characters.map { loadCharacterById(it) }
        } else {
            return emptyList()
        }
    }

    override fun loadCharacterById(characterId: Int): GameCharacter {
        return objectMapper.readValue(File(baseDir, "characters/$characterId.json"), JsonCharacter::class.java)
    }

    override fun save(character: GameCharacter): Int {
        if (character !is JsonCharacter) {
            throw IllegalArgumentException("Character is not a JsonCharacter")
        }

        var effectiveId = character.id
        if (effectiveId == 0) {
            val files = File(baseDir, "characters").listFiles()
            if (files != null) {
                effectiveId = files.maxOfOrNull { it.nameWithoutExtension.toInt() } ?: 0
            }
            effectiveId++
            character.id = effectiveId
        }

        val characterFile = File(baseDir, "characters/${effectiveId}.json")
        characterFile.parentFile.mkdirs()
        objectMapper.writeValue(characterFile, character)

        val accountFile = File(baseDir, "accounts/${character.accountId}.json")
        accountFile.parentFile.mkdirs()
        if (accountFile.exists()) {
            val account = objectMapper.readValue(accountFile, JsonAccount::class.java)
            account.characters.add(effectiveId)
            objectMapper.writeValue(accountFile, account)
        } else {
            val account = JsonAccount(mutableSetOf(effectiveId))
            objectMapper.writeValue(accountFile, account)
        }

        return effectiveId
    }

    override fun isLocked(characterId: Int): Boolean {
        return locks.contains(characterId)
    }

    override fun lock(characterId: Int) {
        if (locks.contains(characterId)) {
            throw IllegalStateException("Character $characterId is already locked")
        }
        locks.add(characterId)
    }

    override fun unlock(characterId: Int) {
        locks.remove(characterId)
    }

    override fun persist(entity: Entity): GameCharacter {
        val persistenceComponent = persistenceMapper[entity] ?: DatabasePersistenceComponent().also(entity::add)
        val nameComponent = nameMapper[entity]
        val transformComponent = transformMapper[entity]
        val raceComponent = raceMapper[entity]
        val colorComponent = colorMapper[entity]
        val visualTraitsComponent = visualTraitsMapper[entity]
        val statsComponent = statsMapper[entity]
        val inventoryComponent = inventoryMapper[entity]
        val processComponent = processMapper[entity]
        val traitsComponent = traitsMapper[entity]
        return JsonCharacter(persistenceComponent.accountId, persistenceComponent.charId, nameComponent?.name ?: "", raceComponent?.race?.id(registries) ?: 1).apply {
            position = transformComponent?.position ?: Vector3Int.Zero
            direction = transformComponent?.direction ?: GridDirection.South
            skinColor = colorComponent?.color?.toIntRGBA() ?: Integer.MAX_VALUE
            originalRaceId = raceComponent?.originalRace?.id(registries) ?: 1
            visualTraitsComponent?.visualTraits?.let { visualTraits.addAll(it) }
            statsComponent?.statValues?.let { stats.putAll(it.mapKeys { (key, _) -> key.id(registries) ?: 0 }) }
            inventoryComponent?.inventoryIds?.let { inventoryIds.putAll(it) }
            processComponent?.passiveContexts?.let {
                processComponent.passiveContexts.stream().filter { !it.process.transient }.map {
                    ProcessInstanceData(
                        registries.processes.getId(it.process.name) ?: 0,
                        it.contextData,
                        it.taskData,
                        it.checkpoint
                    )
                }.forEach(processes::add)
            }
            traitsComponent?.traits?.stream()?.filter { !it.transient }?.map {
                TraitInstanceData(
                    registries.traits.getId(it.trait.name) ?: 0,
                    it.data
                )
            }?.forEach(traits::add)
        }
    }
}