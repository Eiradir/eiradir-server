package net.eiradir.server.map.sync

import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.entity.*
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.MapViewComponent
import net.eiradir.server.entity.event.*
import net.eiradir.server.map.*
import net.eiradir.server.network.event.NetworkRegisterPacketsEvent
import net.eiradir.server.map.event.TileChunkUpdatedEvent
import net.eiradir.server.map.event.TileUpdatedEvent
import net.eiradir.server.map.ClientMapView
import net.eiradir.server.entity.network.*
import net.eiradir.server.map.event.MapLoadedEvent
import net.eiradir.server.map.event.MapUnloadedEvent
import net.eiradir.server.camera.*
import net.eiradir.server.map.MapManager
import net.eiradir.server.network.ServerNetworkContext
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.component.inject

class MapSyncServerPlugin : EiradirPlugin {

    private val mapManager by inject<MapManager>()
    private val entityLocationCache by inject<EntityLocationCache>()
    private val cameraService by inject<CameraService>()
    private val advancedEncoders by inject<AdvancedEncoders>()

    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val mapViewMapper = mapperFor<MapViewComponent>()

    private fun sendMergedChunk(client: ServerNetworkContext, chunkPos: ChunkDimensions) {
        val chunk = mapManager.mergedMap.getChunkAt(chunkPos) ?: return mapManager.desireChunk(chunkPos)
        client.send(TileMapPacket(MapManager.MERGED, chunkPos, chunk.getBackingArray()))
    }

    private fun sendScopedChunks(client: ServerNetworkContext, chunkPos: ChunkDimensions) {
        val entity = client.connectionEntity ?: return
        val mapView = mapViewMapper[entity]?.mapView as? ClientMapView ?: return
        for (map in mapView.scopedMaps) {
            val chunk = map.getChunkAt(chunkPos) ?: continue
            client.send(TileMapPacket(map.name, chunkPos, chunk.getBackingArray()))
        }
    }

    private fun sendEntities(client: ServerNetworkContext, map: EiradirMap, chunkPos: ChunkDimensions) {
        val entities = entityLocationCache.getEntitiesIn(map, chunkPos)
        if (!entities.isEmpty()) {
            entities.chunked(EntitiesAddPacket.MAX_ENTITIES_PER_PACKET).forEach { partial ->
                client.send(EntitiesAddPacket(map.name, chunkPos, partial.map { advancedEncoders.createNetworkedEntity(it) }))
            }
        }
    }

    private fun sendMergedEntities(client: ServerNetworkContext, chunkPos: ChunkDimensions) {
        for (loadedMap in mapManager.loadedMaps) {
            sendEntities(client, loadedMap, chunkPos)
        }
    }

    private fun sendScopedEntities(client: ServerNetworkContext, chunkPos: ChunkDimensions) {
        val entity = client.connectionEntity ?: return
        val mapView = mapViewMapper[entity]?.mapView as? ClientMapView ?: return
        for (map in mapView.scopedMaps) {
            sendEntities(client, map, chunkPos)
        }
    }

    @Subscribe
    fun onWatchChunk(event: WatchChunkEvent) {
        val client = event.client ?: return
        sendMergedChunk(client, event.chunkPos)
        sendScopedChunks(client, event.chunkPos)
        sendMergedEntities(client, event.chunkPos)
        sendScopedEntities(client, event.chunkPos)
    }

    @Subscribe
    fun onUnwatchChunk(event: UnwatchChunkEvent) {
        mapManager.freeDesiredChunk(event.chunkPos)
    }

    @Subscribe
    fun onMapLoaded(event: MapLoadedEvent) {
        val mapView = event.mapView as? ClientMapView ?: return
        val watchedChunks = mapView.client.connectionEntity?.let { cameraService.getWatchedChunks(it) } ?: return
        for (chunkPos in watchedChunks) {
            sendScopedChunks(mapView.client, chunkPos)
            sendScopedEntities(mapView.client, chunkPos)
        }
    }

    @Subscribe
    fun onMapUnloaded(event: MapUnloadedEvent) {
        val mapView = event.mapView as? ClientMapView ?: return
        mapView.client.send(MapUnloadPacket(event.map.name))
    }

    @Subscribe
    fun onRegisterPackets(event: NetworkRegisterPacketsEvent) {
        event.registerPacket(TileMapPacket::class, TileMapPacket::encode, TileMapPacket::decode)
        event.registerPacket(TileUpdatePacket::class, TileUpdatePacket::encode, TileUpdatePacket::decode)
        event.registerPacket(EntitiesAddPacket::class, EntitiesAddPacket::encode, EntitiesAddPacket::decode)
        event.registerPacket(EntitiesRemovePacket::class, EntitiesRemovePacket::encode, EntitiesRemovePacket::decode)
        event.registerPacket(EntityAddPacket::class, EntityAddPacket::encode, EntityAddPacket::decode)
        event.registerPacket(EntityRemovePacket::class, EntityRemovePacket::encode, EntityRemovePacket::decode)
        event.registerPacket(EntityMovePacket::class, EntityMovePacket::encode, EntityMovePacket::decode)
        event.registerPacket(EntityDirectionPacket::class, EntityDirectionPacket::encode, EntityDirectionPacket::decode)
        event.registerPacket(EntitySwitchMapPacket::class, EntitySwitchMapPacket::encode, EntitySwitchMapPacket::decode)
        event.registerPacket(MapUnloadPacket::class, MapUnloadPacket::encode, MapUnloadPacket::decode)
    }

    @Subscribe
    fun onChunkUpdated(event: TileChunkUpdatedEvent) {
        cameraService.sendToWatching(event.map, event.chunkPos, TileMapPacket(event.map.name, event.chunkPos, event.tiles))
    }

    @Subscribe
    fun onTileUpdated(event: TileUpdatedEvent) {
        cameraService.sendToWatching(event.map, event.chunkPos, TileUpdatePacket(event.map.name, event.position, event.tile))
    }

    @Subscribe
    fun onEntityAdded(event: EntityAddedEvent) {
        cameraService.sendToWatching(event.map, event.chunkPos, EntityAddPacket(event.map.name, advancedEncoders.createNetworkedEntity(event.entity)))
    }

    @Subscribe
    fun onEntityMerged(event: EntityMergedEvent) {
        cameraService.sendToWatching(event.map, event.chunkPos, EntityAddPacket(event.map.name, advancedEncoders.createNetworkedEntity(event.entity)))
    }

    @Subscribe
    fun onEntityPositionChanged(event: EntityPositionChangedEvent) {
        val entityId = idMapper[event.entity]?.id ?: return
        val oldChunkPos = event.map.dimensions.of(event.oldPosition)
        val newChunkPos = event.map.dimensions.of(event.position)
        val affectedChunks = setOf(oldChunkPos, newChunkPos)
        cameraService.sendToWatching(getEffectiveMap(event.map), affectedChunks, EntityMovePacket(entityId, event.position))
    }

    @Subscribe
    fun onEntityDirectionChanged(event: EntityDirectionChangedEvent) {
        val entityId = idMapper[event.entity]?.id ?: return
        val position = transformMapper[event.entity]?.position ?: return
        val chunkPos = event.map.dimensions.of(position)
        val affectedChunks = setOf(chunkPos)
        cameraService.sendToWatching(getEffectiveMap(event.map), affectedChunks, EntityDirectionPacket(entityId, event.direction))
    }

    @Subscribe
    fun onEntitiesAdded(event: EntitiesAddedEvent) {
        event.entities.chunked(EntitiesAddPacket.MAX_ENTITIES_PER_PACKET).forEach { partial ->
            cameraService.sendToWatching(
                event.map,
                event.chunkPos,
                EntitiesAddPacket(event.map.name, event.chunkPos, partial.map { advancedEncoders.createNetworkedEntity(it) })
            )
        }
    }

    @Subscribe
    fun onEntitiesMerged(event: EntitiesMergedEvent) {
        event.entities.chunked(EntitiesAddPacket.MAX_ENTITIES_PER_PACKET).forEach { partial ->
            cameraService.sendToWatching(
                event.map,
                event.chunkPos,
                EntitiesAddPacket(event.map.name, event.chunkPos, partial.map { advancedEncoders.createNetworkedEntity(it) })
            )
        }
    }

    private fun getMapOrMerged(map: EiradirMap): EiradirMap {
        if (mapManager.isLoaded(map)) {
            return mapManager.mergedMap
        } else {
            return map
        }
    }

    @Subscribe
    fun onEntityMapChanged(event: EntitySwitchedMapEvent) {
        val transform = transformMapper[event.entity] ?: throw IllegalStateException("Entity ${idMapper[event.entity]?.id} has no transform")
        val oldChunkPos = event.oldMap.dimensions.of(transform.position)
        val newChunkPos = event.newMap.dimensions.of(transform.position)

        // Send EntityRemove to clients that do not have the target map loaded (either scoped or via merged)
        cameraService.sendToWatching(oldChunkPos, {
            val mapView = mapViewMapper[it]?.mapView as? ClientMapView ?: return@sendToWatching false
            !mapView.isLoadedOrMerged(event.newMap)
        }, EntityRemovePacket(getMapOrMerged(event.oldMap).name, idMapper[event.entity].id))

        // Send EntityAdd to clients that have the new map loaded, but did not have the old map loaded (either scoped or via merged)
        cameraService.sendToWatching(newChunkPos, {
            val mapView = mapViewMapper[it]?.mapView as? ClientMapView ?: return@sendToWatching false
            !mapView.isLoadedOrMerged(event.oldMap) && mapView.isLoadedOrMerged(event.newMap)
        }, EntityAddPacket(getMapOrMerged(event.newMap).name, advancedEncoders.createNetworkedEntity(event.entity)))

        // Send EntitySwitchMap to clients that have both the new and old map loaded (either scoped or via merged)
        val entityId = idMapper[event.entity]?.id ?: throw IllegalStateException("Entity has no ID")
        cameraService.sendToWatching(newChunkPos, {
            val mapView = mapViewMapper[it]?.mapView as? ClientMapView ?: return@sendToWatching false
            mapView.isLoadedOrMerged(event.oldMap) && mapView.isLoadedOrMerged(event.newMap)
        }, EntitySwitchMapPacket(entityId, getMapOrMerged(event.newMap).name))
    }

    @Subscribe
    fun onEntityRemoved(event: EntityRemovedEvent) {
        val entityId = idMapper[event.entity]?.id ?: return
        cameraService.sendToWatching(event.map, event.chunkPos, EntityRemovePacket(event.map.name, entityId))
    }

    @Subscribe
    fun onEntitiesRemoved(event: EntitiesRemovedEvent) {
        val entityIds = event.entities.map { idMapper[it]?.id ?: throw IllegalStateException("Entity has no ID") }
        entityIds.chunked(EntitiesRemovePacket.MAX_ENTITIES_PER_PACKET).forEach { partial ->
            cameraService.sendToWatching(event.map, event.chunkPos, EntitiesRemovePacket(event.map.name, event.chunkPos, partial))
        }
    }

    @Subscribe
    fun onEntityUnmerged(event: EntityUnmergedEvent) {
        val entityId = idMapper[event.entity]?.id ?: return
        cameraService.sendToWatching(event.map, event.chunkPos, EntityRemovePacket(event.map.name, entityId))
    }

    @Subscribe
    fun onEntitiesUnmerged(event: EntitiesUnmergedEvent) {
        val entityIds = event.entities.map { idMapper[it]?.id ?: throw IllegalStateException("Entity has no ID") }
        entityIds.chunked(EntitiesRemovePacket.MAX_ENTITIES_PER_PACKET).forEach { partial ->
            cameraService.sendToWatching(event.map, event.chunkPos, EntitiesRemovePacket(event.map.name, event.chunkPos, partial))
        }
    }

    private fun getEffectiveMap(map: EiradirMap): EiradirMap {
        if (mapManager.isLoaded(map)) {
            return mapManager.mergedMap
        } else {
            return map
        }
    }
}