package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.culling.EntitiesCulledEvent
import net.eiradir.server.culling.EntitiesUnculledEvent
import net.eiradir.server.culling.EntityCullingResolver
import net.eiradir.server.data.Tile
import net.eiradir.server.entity.components.*
import net.eiradir.server.entity.event.*
import net.eiradir.server.item.ItemComponent
import net.eiradir.server.map.*
import net.eiradir.server.map.event.MapLoadedEvent
import net.eiradir.server.map.event.MapUnloadedEvent
import net.eiradir.server.map.event.TileChunkUpdatedEvent
import net.eiradir.server.map.event.TileUpdatedEvent
import net.eiradir.server.map.filter.RemovedByFilter
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.registry.Registries
import net.eiradir.server.map.MapManager
import java.util.UUID

class EntityMergeManager(
    private val mapManager: MapManager,
    private val registries: Registries,
    private val entityLocationCache: EntityLocationCache,
    private val entityCullingResolver: EntityCullingResolver,
    private val eventBus: EventBus
) : EventBusSubscriber {

    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val mapReferenceMapper = mapperFor<net.eiradir.server.entity.components.MapReference>()
    private val itemMapper = mapperFor<ItemComponent>()
    private val removedByFilter = mapperFor<RemovedByFilter>()

    private val mergedEntities = mutableMapOf<UUID, Entity>()

    @Subscribe
    fun onEntityAdded(event: EntityAddedEvent) {
        if (shouldMergeFrom(event.map)) {
            val mergeResult = redoMerge(event.entity)
            if (mergeResult.shouldMerge()) {
                eventBus.post(EntityMergedEvent(mapManager.mergedMap, event.chunkPos, event.entity))
            }
        }
    }

    @Subscribe
    fun onEntityRemoved(event: EntityRemovedEvent) {
        if (isEntityMerged(event.entity)) {
            unmerge(event.entity)
            eventBus.post(EntityUnmergedEvent(mapManager.mergedMap, event.chunkPos, event.entity))
        }
    }

    @Subscribe
    fun onEntityRemovedNonDestructively(event: EntityRemovedNonDestructivelyEvent) {
        if (isEntityMerged(event.entity)) {
            unmerge(event.entity)
            eventBus.post(EntityUnmergedEvent(mapManager.mergedMap, event.chunkPos, event.entity))
        }
    }

    @Subscribe
    fun onEntitiesAdded(event: EntitiesAddedEvent) {
        if (shouldMergeFrom(event.map)) {
            val mergedEntities = event.entities.asSequence().map { redoMerge(it) }.filter { it.shouldMerge() }.map { it.mergedEntity }.toList()
            if (mergedEntities.isNotEmpty()) {
                eventBus.post(EntitiesMergedEvent(mapManager.mergedMap, event.chunkPos, mergedEntities))
            }
        }
    }

    @Subscribe
    fun onEntitiesRemoved(event: EntitiesRemovedEvent) {
        val mergedEntities = event.entities.filter { isEntityMerged(it) }
        mergedEntities.forEach {
            unmerge(it)
        }
        if (mergedEntities.isNotEmpty()) {
            eventBus.post(EntitiesUnmergedEvent(mapManager.mergedMap, event.chunkPos, mergedEntities))
        }
    }

    @Subscribe
    fun onEntitiesCulled(event: EntitiesCulledEvent) {
        val mergedEntities = event.entities.filter { isEntityMerged(it) }
        mergedEntities.forEach {
            unmerge(it)
        }
        if (mergedEntities.isNotEmpty()) {
            eventBus.post(EntitiesUnmergedEvent(mapManager.mergedMap, event.chunkPos, mergedEntities))
        }
    }

    @Subscribe
    fun onEntitiesUnculled(event: EntitiesUnculledEvent) {
        if (shouldMergeFrom(event.map)) {
            val mergedEntities = event.entities.asSequence().map { redoMerge(it) }.filter { it.shouldMerge() }.map { it.mergedEntity }.toList()
            if (mergedEntities.isNotEmpty()) {
                eventBus.post(EntitiesMergedEvent(mapManager.mergedMap, event.chunkPos, mergedEntities))
            }
        }
    }

    @Subscribe
    fun onEntitySwitchedMap(event: EntitySwitchedMapEvent) {
        // We do not fire merge events in this method because EntitySwitchedMapEvent is handled as a special case by the sync system
        if (shouldMergeFrom(event.newMap)) {
            redoMerge(event.entity)
        } else if (isEntityMerged(event.entity)) {
            unmerge(event.entity)
        }
    }

    enum class EntityMergeResultType {
        MERGED,
        UNMERGED,
        SWAPPED,
        NO_CHANGE
    }

    data class EntityMergeResult(val result: EntityMergeResultType, val entity: Entity, val mergedEntity: Entity) {
        fun shouldMerge(): Boolean {
            return when (result) {
                EntityMergeResultType.MERGED, EntityMergeResultType.SWAPPED -> true
                else -> false
            }
        }

        companion object {
            fun unmerged(entity: Entity) = EntityMergeResult(EntityMergeResultType.UNMERGED, entity, entity)
            fun merged(entity: Entity, mergedEntity: Entity) = EntityMergeResult(EntityMergeResultType.MERGED, entity, mergedEntity)
            fun swapped(entity: Entity, mergedEntity: Entity) = EntityMergeResult(EntityMergeResultType.SWAPPED, entity, mergedEntity)
            fun noChange(entity: Entity) = EntityMergeResult(EntityMergeResultType.NO_CHANGE, entity, entity)
        }
    }

    private fun redoMerge(entity: Entity): EntityMergeResult {
        val id = idMapper[entity]?.id ?: throw IllegalArgumentException("Entity does not have an ID component")
        val mergedEntity = getMergedEntity(id)
        val mappedEntity = getFilteredEntity(entity)
        val effectiveEntity = mappedEntity ?: entity
        val isMerged = isEntityMerged(effectiveEntity)
        val preventMerge = mappedEntity == null || shouldMergeBePrevented(entity)
        when {
            !isMerged && !preventMerge && mergedEntity != null -> {
                mapManager.mergedMap.engineQueue?.removeEntity(mergedEntity)
                mapManager.mergedMap.engineQueue?.addEntity(effectiveEntity)
                mergedEntities[id] = effectiveEntity
                return EntityMergeResult.swapped(entity, effectiveEntity)
            }

            isMerged && preventMerge -> {
                mapManager.mergedMap.engineQueue?.removeEntity(entity)
                mergedEntities.remove(id)
                return EntityMergeResult.unmerged(entity)
            }

            !isMerged && !preventMerge -> {
                mapManager.mergedMap.engineQueue?.addEntity(effectiveEntity)
                mergedEntities[id] = effectiveEntity
                return EntityMergeResult.merged(entity, effectiveEntity)
            }

            else -> {
                return EntityMergeResult.noChange(entity)
            }
        }
    }

    private fun getFilteredEntity(entity: Entity): Entity? {
        val map = mapReferenceMapper[entity]?.map ?: return null
        val filter = mapManager.getMapFilterAppliedTo(map) ?: return entity
        return filter.mapEntity(registries, entity)
    }

    private fun unmerge(entity: Entity) {
        val id = idMapper[entity]?.id ?: throw IllegalArgumentException("Entity does not have an ID component")
        mapManager.mergedMap.engineQueue?.removeEntity(entity)
        mergedEntities.remove(id)
    }

    private fun shouldMergeFrom(map: EiradirMap): Boolean {
        return mapManager.isLoaded(map)
    }

    private fun isEntityMerged(entity: Entity): Boolean {
        val id = idMapper[entity]?.id ?: return false
        return mergedEntities[id] == entity
    }

    private fun getMergedEntity(id: UUID): Entity? {
        return mergedEntities[id]
    }

    private fun redoMergeAndFireEvents(map: EiradirMap, chunkPos: ChunkDimensions, entities: Collection<Entity>) {
        val entityChanges = entities.map { redoMerge(it) }
        val entityChangesByType = entityChanges.groupBy { it.result }
        val mergedEntities = entityChangesByType[EntityMergeResultType.MERGED] ?: emptyList()
        val unmergedEntities = entityChangesByType[EntityMergeResultType.UNMERGED] ?: emptyList()
        val changedEntities = entityChangesByType[EntityMergeResultType.SWAPPED] ?: emptyList()
        if (mergedEntities.isNotEmpty()) {
            eventBus.post(EntitiesMergedEvent(mapManager.mergedMap, chunkPos, mergedEntities.map { it.mergedEntity }))
        }
        if (unmergedEntities.isNotEmpty()) {
            eventBus.post(EntitiesUnmergedEvent(mapManager.mergedMap, chunkPos, unmergedEntities.map { it.mergedEntity }))
        }
        if (changedEntities.isNotEmpty()) {
            eventBus.post(EntitiesUnmergedEvent(mapManager.mergedMap, chunkPos, changedEntities.map { it.entity }))
            eventBus.post(EntitiesMergedEvent(mapManager.mergedMap, chunkPos, changedEntities.map { it.mergedEntity }))
        }
    }

    private fun redoMergeAndFireEventsForLoadedMaps(chunkPos: ChunkDimensions) {
        for (loadedMap in mapManager.loadedMaps) {
            val entities = entityLocationCache.getEntitiesIn(loadedMap, chunkPos)
            redoMergeAndFireEvents(loadedMap, chunkPos, entities)
        }
    }

    @Subscribe
    fun onMapLoaded(event: MapLoadedEvent) {
        if (event.map.removalList != null) {
            for (loadedChunk in mapManager.mergedMap.getLoadedChunks()) {
                redoMergeAndFireEventsForLoadedMaps(loadedChunk.dimensions)
            }
        }
    }

    @Subscribe
    fun onMapUnloaded(event: MapUnloadedEvent) {
        if (event.map.removalList != null) {
            for (loadedChunk in mapManager.mergedMap.getLoadedChunks()) {
                redoMergeAndFireEventsForLoadedMaps(loadedChunk.dimensions)
            }
        }
    }

    @Subscribe
    fun onChunkUpdated(event: TileChunkUpdatedEvent) {
        // Tile updates are only relevant if they are merged into the merged map
        if (!mapManager.isMergedMap(event.map)) {
            return
        }

        redoMergeAndFireEventsForLoadedMaps(event.chunkPos)
    }

    @Subscribe
    fun onTileUpdated(event: TileUpdatedEvent) {
        // Tile updates are only relevant if they are merged into the merged map
        if (!mapManager.isMergedMap(event.map)) {
            return
        }

        for (loadedMap in mapManager.loadedMaps) {
            val entities = entityLocationCache.getEntitiesAt(loadedMap, event.position)
            redoMergeAndFireEvents(loadedMap, event.chunkPos, entities)
        }
    }

    private fun shouldAutoClear(entity: Entity): Boolean {
        return itemMapper.has(entity)
    }

    private fun isClearedExplicitly(entity: Entity): Boolean {
        return mapManager.loadedMaps.any { it.isEntityRemoved(entity) }
    }

    private fun isAnyMapLoadedAfterAutoClearing(entity: Entity): Boolean {
        // Some entities are auto-cleared if a map loaded after them has a tile at the same position
        val map = mapReferenceMapper[entity]?.map ?: return false
        val position = transformMapper[entity]?.position ?: return false
        val mapIndex = mapManager.loadedMaps.indexOf(map)
        if (mapIndex >= 0) {
            val mapsLoadedAfter = mapManager.loadedMaps.subList(mapIndex + 1, mapManager.loadedMaps.size)
            for (mapLoadedAfter in mapsLoadedAfter) {
                if (isAutoClearing(mapLoadedAfter, position)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isAutoClearing(map: EiradirMap, position: Vector3Int): Boolean {
        val overridingTile = map.getChunkAt(position)?.getTileAt(position) ?: return false
        return overridingTile != Tile.Invalid
    }

    private fun shouldMergeBePrevented(entity: Entity): Boolean {
        return isClearedExplicitly(entity) || (shouldAutoClear(entity) && isAnyMapLoadedAfterAutoClearing(entity)) || removedByFilter.has(entity)
    }

}