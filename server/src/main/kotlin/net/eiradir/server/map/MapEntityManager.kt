package net.eiradir.server.map

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.entity.EntityLocationCache
import net.eiradir.server.entity.event.EntitiesAddedEvent
import net.eiradir.server.entity.event.EntitiesRemovedEvent
import net.eiradir.server.entity.event.EntityAddedEvent
import net.eiradir.server.entity.event.EntityRemovedEvent
import net.eiradir.server.extensions.logger
import net.eiradir.server.map.event.ChunkLoadedEvent
import net.eiradir.server.map.event.ChunkUnloadedEvent
import net.eiradir.server.map.event.MapLoadedEvent
import net.eiradir.server.map.event.MapUnloadedEvent
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.lifecycle.ServerSaveEvent

class MapEntityManager(
    private val engine: Engine,
    private val mapManager: MapManager,
    private val scopedMapManager: ScopedMapManager,
    private val entityPersistence: EntityPersistence,
    private val entityLocationCache: EntityLocationCache,
    private val eventBus: EventBus
) : EventBusSubscriber {

    private val log = logger()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()

    @Subscribe
    fun onMapLoaded(event: MapLoadedEvent) {
        for (chunk in event.map.getLoadedChunks()) {
            loadEntities(event.map, chunk.dimensions)
        }
    }

    @Subscribe
    fun onChunkLoaded(event: ChunkLoadedEvent) {
        loadEntities(event.map, event.dimensions)
    }

    @Subscribe
    fun onChunkUnloaded(event: ChunkUnloadedEvent) {
        entityPersistence.saveDirtyEntitiesAt(event.map, event.dimensions)
    }

    @Subscribe
    fun onMapUnloaded(event: MapUnloadedEvent) {
        val unloadedEntitiesByChunk = unloadEntities(event.map)
        for (entry in unloadedEntitiesByChunk) {
            eventBus.post(EntitiesRemovedEvent(event.map, entry.key, entry.value))
        }

        entityPersistence.saveDirtyEntitiesFrom(event.map)
    }

    @Subscribe
    fun onServerSaved(event: ServerSaveEvent) {
        for (map in mapManager.loadedMaps) {
            for (loadedChunk in map.getLoadedChunks()) {
                entityPersistence.saveDirtyEntitiesAt(map, loadedChunk.dimensions)
            }
        }

        for (map in scopedMapManager.scopedMaps.values) {
            for (loadedChunk in map.getLoadedChunks()) {
                entityPersistence.saveDirtyEntitiesAt(map, loadedChunk.dimensions)
            }
        }
    }

    private fun loadEntities(map: EiradirMap, chunkPos: ChunkDimensions) {
        val entities = entityPersistence.loadEntities(map, chunkPos) {
            it.add(net.eiradir.server.entity.components.MapReference().apply { this.map = map })
            map.engineQueue?.addEntity(it)
        }
        if (entities.isNotEmpty()) {
            eventBus.post(EntitiesAddedEvent(map, chunkPos, entities))
        }
    }

    private fun unloadEntities(map: EiradirMap): Map<ChunkDimensions, Collection<Entity>> {
        return map.getLoadedChunks().associate { it.dimensions to entityLocationCache.getEntitiesIn(map, it.dimensions) }
    }

    fun addEntity(map: EiradirMap, entity: Entity) {
        log.info("Adding entity $entity to map $map")
        val position = transformMapper[entity]?.position ?: throw IllegalStateException("Entity has no transform")
        map.engineQueue?.addEntity(entity)
        eventBus.post(EntityAddedEvent(map, map.dimensions.of(position), entity))
    }

    fun removeEntity(map: EiradirMap, entity: Entity) {
        log.info("Removing entity $entity from map $map")
        val position = transformMapper[entity]?.position ?: throw IllegalStateException("Entity has no transform")
        map.engineQueue?.removeEntity(entity)
        eventBus.post(EntityRemovedEvent(map, map.dimensions.of(position), entity))
    }
}