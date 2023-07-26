package net.eiradir.server.entity

import arrow.core.Either
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.camera.entity.CameraComponent
import net.eiradir.server.data.IsoType
import net.eiradir.server.entity.components.*
import net.eiradir.server.entity.event.EntityPositionChangedEvent
import net.eiradir.server.entity.event.EntityRemovedNonDestructivelyEvent
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.item.ItemInstance
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.map.MapManager
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.player.GameCharacter
import net.eiradir.server.registry.Registries
import net.eiradir.server.map.MapEntityManager
import net.eiradir.server.math.GridDirection
import net.eiradir.server.persistence.DatabasePersistenceComponent
import net.eiradir.server.process.entity.ProcessComponent
import net.eiradir.server.process.ProcessContext
import net.eiradir.server.services
import net.eiradir.server.stats.entity.StatsComponent
import net.eiradir.server.trait.TraitInstance
import java.util.UUID

val NULL_UUID = UUID(0, 0)

class EntityService(
    private val registries: Registries,
    private val mapManager: MapManager,
    private val mapEntityManager: MapEntityManager,
    private val eventBus: EventBus,
    private val engine: Engine
) {
    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<GridTransform>()
    private val cameraMapper = mapperFor<CameraComponent>()
    private val mapReferenceMapper = mapperFor<MapReference>()

    fun getEntityId(entity: Entity, idComponent: IdComponent? = idMapper[entity]): UUID {
        return idComponent?.id ?: return NULL_UUID
    }

    fun getEntityPosition(entity: Entity): Vector3Int {
        return transformMapper[entity]?.position ?: cameraMapper[entity]?.position ?: Vector3Int.Zero
    }

    fun setEntityPosition(entity: Entity, pos: Vector3Int) {
        val transform = transformMapper[entity] ?: return
        val map = mapReferenceMapper[entity]?.map
        val oldPosition = transform.position
        transform.position = pos
        if (map != null) {
            eventBus.post(EntityPositionChangedEvent(entity, map, pos, oldPosition))
        }
    }

    fun getEntityMap(entity: Entity): EiradirMap? {
        return mapReferenceMapper[entity]?.map
    }

    fun createEntity(character: GameCharacter, persist: Boolean): Either<EntityCreationError, Entity> {
        val race = registries.races.getById(character.raceId) ?: return Either.Left(EntityCreationError.InvalidRace)
        val stats = character.stats.mapNotNull { (key, value) -> registries.stats.getById(key)?.let { it to value } }.toMap()
        val originalRace = registries.races.getById(character.originalRaceId) ?: race
        return createEntity(race.isoId(registries), character.position, character.direction).tap { entity ->
            entity.add(NameComponent(character.name))
            entity.add(RaceComponent(race).apply { this.originalRace = originalRace })
            entity.add(InventoryComponent(character.inventoryIds.toMutableMap()))
            entity.add(StatsComponent().apply {
                this.statValues.putAll(stats)
            })
            entity.add(ColorComponent(Color(character.skinColor)))
            if (character.visualTraits.isNotEmpty()) {
                entity.add(VisualTraitsComponent().apply {
                    this.visualTraits.addAll(character.visualTraits)
                })
            }
            if (character.processes.isNotEmpty()) {
                entity.add(ProcessComponent().apply {
                    for (processData in character.processes) {
                        val processType = registries.processes.getById(processData.id) ?: continue
                        this.passiveContexts.add(ProcessContext(entity, processType.process, processData.checkpoint, processData.contextData))
                    }
                })
            }
            if (character.traits.isNotEmpty()) {
                for (traitData in character.traits) {
                    val trait = registries.traits.getById(traitData.id) ?: continue
                    entity.services().traits.addTrait(entity, TraitInstance(trait, traitData.data, false))
                }
            }
            if (persist) {
                entity.add(DatabasePersistenceComponent(character.accountId, character.id))
            }
        }
    }

    fun createEntity(typeName: String, position: Vector3Int, direction: GridDirection = GridDirection.South): Either<EntityCreationError, Entity> {
        val isoType = getIsoType(typeName) ?: return Either.Left(EntityCreationError.InvalidIsoType)
        return createEntity(isoType.isoId(registries), position, direction)
    }

    fun createEntity(itemInstance: ItemInstance, position: Vector3Int): Either<EntityCreationError, Entity> {
        return createEntity(itemInstance.item.isoId(registries), position).tap {
            it.add(ItemComponent().apply { this.itemInstance = itemInstance })
        }
    }

    fun createEntity(isoId: Int, position: Vector3Int, direction: GridDirection = GridDirection.South): Either<EntityCreationError, Entity> {
        val entity = engine.createEntity()
        val uniqueId = UUID.randomUUID()
        entity.add(IdComponent(uniqueId))
        entity.add(GridTransform().apply {
            this.position = position
            this.direction = direction
        })
        entity.add(IsoComponent().apply { this.isoId = isoId })
        return Either.Right(entity)
    }

    fun spawnEntity(entity: Entity, map: EiradirMap = mapManager.getDefaultMap()) {
        entity.add(MapReference().apply {
            this.map = map
        })
        mapEntityManager.addEntity(map, entity)
    }

    private fun getIsoType(name: String): IsoType? {
        return registries.races.getByName(name) ?: registries.items.getByName(name)
    }

    fun removeFromMap(entity: Entity, map: EiradirMap) {
        val entityMap = getEntityMap(entity) ?: throw IllegalArgumentException("Entity is not attached to a map")
        if (entityMap == map) {
            mapEntityManager.removeEntity(map, entity)
        } else {
            map.removeEntityNonDestructively(entity)
            eventBus.post(EntityRemovedNonDestructivelyEvent(map, map.dimensions.of(getEntityPosition(entity)), entity))
        }
    }

    fun removeEntity(entity: Entity) {
        val map = getEntityMap(entity) ?: throw IllegalArgumentException("Entity is not attached to a map")
        mapEntityManager.removeEntity(map, entity)
    }

}