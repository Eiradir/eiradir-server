package net.eiradir.server.map

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.entity.EntityLocationCache
import net.eiradir.server.entity.components.MapViewComponent
import net.eiradir.server.lifecycle.ServerSaveEvent
import net.eiradir.server.lifecycle.ServerStartedEvent
import net.eiradir.server.map.event.MapUnloadedEvent
import net.eiradir.server.map.generator.ImageToWorldConverter
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.session.event.ClientJoinedEvent

class MapEvents(
    private val config: ServerConfig,
    private val mapManager: MapManager,
    private val scopedMapManager: ScopedMapManager,
    private val mapService: MapService,
    private val entityLocationCache: EntityLocationCache,
    private val eventBus: EventBus
) : EventBusSubscriber {
    @Subscribe
    fun onServerStarted(event: ServerStartedEvent) {
        config.maps.forEach {
            mapManager.load(mapService.loadMapFromDisk(it))
        }
        if (!mapManager.isLoaded(mapManager.defaultMap)) {
            mapManager.load(mapService.loadMapFromDisk(mapManager.defaultMap))
        }

        val baseMap = mapManager.getLoadedMapByName("base")
        if (baseMap != null && baseMap.isEmpty()) {
            javaClass.getResourceAsStream("/map-base.png")?.use {
                ImageToWorldConverter.run(it, config.mapsDirectory, "base")
            }
        }
    }

    @Subscribe
    fun onClientJoined(event: ClientJoinedEvent) {
        event.client.connectionEntity?.add(MapViewComponent(ClientMapView(mapManager, scopedMapManager, entityLocationCache, eventBus, event.client)))
    }

    @Subscribe
    fun onServerSaved(event: ServerSaveEvent) {
        mapManager.loadedMaps.forEach(mapService::saveMap)
        scopedMapManager.scopedMaps.values.forEach(mapService::saveMap)
    }

    @Subscribe
    fun onMapUnloaded(event: MapUnloadedEvent) {
        mapService.saveMap(event.map)
    }
}