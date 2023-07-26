package net.eiradir.server.map

import com.fasterxml.jackson.databind.ObjectMapper
import net.eiradir.server.config.ServerConfig
import net.eiradir.server.entity.EngineQueue
import net.eiradir.server.map.filter.MapFilter
import net.eiradir.server.map.tilemap.PersistentChunkedMap
import net.eiradir.server.registry.Registries
import java.io.File

class MapService(
    private val config: ServerConfig,
    private val registries: Registries,
    private val engineQueue: EngineQueue
) {
    fun loadMapFromDisk(name: String): EiradirMap {
        return EiradirMap(
            name,
            PersistentChunkedMap(registries, config.mapsDirectory, name),

            ).withFilter(loadMapFilter(name)).reflectToEngine(engineQueue)
            .withEntityRemovalList(loadEntityRemovalList(name)).reflectToEngine(engineQueue)
    }

    private fun loadMapFilter(name: String): MapFilter? {
        val objectMapper = ObjectMapper()
        val mapDirectory = File(config.mapsDirectory, name)
        val mapFilterFile = File(mapDirectory, "filter.json")
        return if (mapFilterFile.exists()) objectMapper.readValue(mapFilterFile, MapFilter::class.java) else null
    }

    private fun loadEntityRemovalList(name: String): EntityRemovalList? {
        val objectMapper = ObjectMapper()
        val mapDirectory = File(config.mapsDirectory, name)
        val removalListFile = File(mapDirectory, "removed_entities.json")
        return if (removalListFile.exists()) objectMapper.readValue(removalListFile, EntityRemovalList::class.java) else null
    }

    fun saveMap(map: EiradirMap) {
        map.saveAllChanged()
        map.removalList?.let { removalList ->
            if (removalList.isDirty) {
                saveEntityRemovalList(map.name, removalList)
                removalList.isDirty = false
            }
        }
    }

    private fun saveEntityRemovalList(name: String, removalList: EntityRemovalList) {
        val objectMapper = ObjectMapper()
        val mapDirectory = File(config.mapsDirectory, name)
        val removalListFile = File(mapDirectory, "removed_entities.json")
        objectMapper.writeValue(removalListFile, removalList)
    }
}