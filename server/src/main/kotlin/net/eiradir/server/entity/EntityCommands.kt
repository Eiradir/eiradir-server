package net.eiradir.server.entity

import arrow.core.Either
import com.badlogic.ashley.core.Entity
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.argument
import net.eiradir.server.commands.arguments.EntityArgument
import net.eiradir.server.commands.arguments.Vector3IntArgument
import net.eiradir.server.commands.literal
import net.eiradir.server.map.view.EditableMapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.player.GameCharacter
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.persistence.CharacterStorage

class EntityCommands(dispatcher: CommandDispatcher<CommandSource>, private val entityService: EntityService, private val characterStorage: CharacterStorage) :
    Initializer {

    init {
        dispatcher.register(
            literal("create").then(
                argument("entity", StringArgumentType.string()).executes {
                    createEntity(it.source, it.source.position, StringArgumentType.getString(it, "entity"))
                }.then(argument("pos", Vector3IntArgument.vector3Int()).executes {
                    createEntity(it.source, Vector3IntArgument.getVector3Int(it, "pos"), StringArgumentType.getString(it, "entity"))
                    1
                })
            )
        )

        dispatcher.register(
            literal("remove").then(
                argument("entity", EntityArgument.entity()).executes {
                    val entity = EntityArgument.getEntity(it, "entity")
                    if (entity == null) {
                        it.source.respond("Entity not found")
                        return@executes 0
                    }
                    removeEntity(it.source, entity)
                }
            )
        )

        dispatcher.register(
            literal("load").then(
                argument("id", IntegerArgumentType.integer()).executes {
                    val charId = IntegerArgumentType.getInteger(it, "id")
                    if (characterStorage.isLocked(charId)) {
                        it.source.respond("Character is already joined")
                        return@executes 0
                    }

                    val character = characterStorage.loadCharacterById(charId)
                    if (character == null) {
                        it.source.respond("Character not found")
                        return@executes 0
                    }
                    createEntity(it.source, character, true).fold({ 1 }, { 0 })
                }
            )
        )

        dispatcher.register(
            literal("loadclone").then(
                argument("id", IntegerArgumentType.integer()).executes {
                    val charId = IntegerArgumentType.getInteger(it, "id")
                    val character = characterStorage.loadCharacterById(charId)
                    if (character == null) {
                        it.source.respond("Character not found")
                        return@executes 0
                    }
                    createEntity(it.source, character, false).fold({ 1 }, { 0 })
                }.then(argument("pos", Vector3IntArgument.vector3Int()).executes {
                    val charId = IntegerArgumentType.getInteger(it, "id")
                    val character = characterStorage.loadCharacterById(charId)
                    if (character == null) {
                        it.source.respond("Character not found")
                        return@executes 0
                    }
                    createEntity(it.source, character, false, Vector3IntArgument.getVector3Int(it, "pos")).fold({ 1 }, { 0 })
                })
            )
        )
    }

    private fun createEntity(source: CommandSource, pos: Vector3Int, isoName: String): Int {
        val mapView = source.mapView
        if (mapView !is EditableMapView) {
            source.respond("Cannot edit maps from this command source")
            return 0
        }

        val map = mapView.getActionableMap(pos)
        if (map == null) {
            source.respond("Actionable map is not loaded")
            return 0
        }

        return entityService.createEntity(isoName, pos)
            .tapLeft {
                when (it) {
                    is EntityCreationError.InvalidIsoType -> source.respond("No iso type with name '$isoName'")
                    is EntityCreationError.InvalidRace -> source.respond("Invalid race")
                    is EntityCreationError.InvalidInventory -> source.respond("Invalid inventory")
                }
            }.tap {
                entityService.spawnEntity(it, map)
                val entityId = entityService.getEntityId(it)
                source.respond("Spawned entity $entityId at $pos")
            }.fold({ 1 }, { 0 })
    }

    private fun createEntity(source: CommandSource, character: GameCharacter, persist: Boolean, pos: Vector3Int? = null): Either<EntityCreationError, Entity> {
        return entityService.createEntity(character, persist)
            .tapLeft {
                when (it) {
                    is EntityCreationError.InvalidRace -> source.respond("Invalid race ${character.raceId}")
                    is EntityCreationError.InvalidIsoType -> source.respond("Invalid iso type")
                    is EntityCreationError.InvalidInventory -> source.respond("Invalid inventory")
                }
            }.tap {
                if (pos != null) {
                    entityService.setEntityPosition(it, pos)
                }
                entityService.spawnEntity(it)
            }.tap {
                val entityId = entityService.getEntityId(it)
                val entityPos = entityService.getEntityPosition(it)
                source.respond("Spawned entity $entityId at $entityPos")
            }
    }

    private fun removeEntity(source: CommandSource, entity: Entity): Int {
        val mapView = source.mapView
        if (mapView !is EditableMapView) {
            source.respond("Cannot edit maps from this command source")
            return 0
        }

        val position = entityService.getEntityPosition(entity)
        val map = mapView.getActionableMap(position)
        if (map == null) {
            source.respond("Actionable map is not loaded")
            return 0
        }

        entityService.removeFromMap(entity, map)
        return 1
    }
}