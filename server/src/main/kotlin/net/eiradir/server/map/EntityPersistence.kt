package net.eiradir.server.map

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.EntityBucket
import net.eiradir.server.entity.EntityDirtyChunkCache
import net.eiradir.server.entity.EntityLocationCache
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.map.entity.EntitySerialization
import net.eiradir.server.map.entity.PersistenceComponent
import net.eiradir.server.config.ServerConfig
import java.io.File

class EntityPersistence(
    private val entitySerialization: EntitySerialization,
    private val entityLocationCache: EntityLocationCache,
    private val entityDirtyChunkCache: EntityDirtyChunkCache,
    private val serverConfig: ServerConfig,
) {

    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<GridTransform>()
    private val mapReferenceMapper = mapperFor<MapReference>()

    private val persistenceMapper = mapperFor<PersistenceComponent>()

    private fun getEntitiesFile(map: EiradirMap, chunkPos: ChunkDimensions): File {
        val mapDirectory = File(serverConfig.mapsDirectory, map.name)
        return File(mapDirectory, "${chunkPos.x}_${chunkPos.y}_${chunkPos.level}.entities")
    }

    fun saveDirtyEntitiesFrom(map: EiradirMap) {
        for (chunk in map.getLoadedChunks()) {
            saveDirtyEntitiesAt(map, chunk.dimensions)
        }
    }

    fun saveDirtyEntitiesAt(map: EiradirMap, chunkPos: ChunkDimensions) {
        val entities = entityLocationCache.getEntitiesIn(map, chunkPos)
        val entitiesInBucket = mutableListOf<Entity>()
        var anyDirty = entityDirtyChunkCache.isDirty(map, chunkPos)
        for (entity in entities) {
            val persistenceComponent = persistenceMapper[entity]
            if (persistenceComponent?.isDirty == true) {
                anyDirty = true
                persistenceComponent.isDirty = false
            }
            if (persistenceComponent?.bucket == EntityBucket.Shared) {
                entitiesInBucket.add(entity)
            }
        }
        if (anyDirty) {
            saveEntitiesAt(entitiesInBucket, map, chunkPos)
        }
    }

    fun saveAllEntities(entities: List<Entity>) {
        val entitiesByChunk = entities.groupBy {
            val position = transformMapper[it]?.position ?: error("Entity ${idMapper[it]?.id} has no position")
            val map = mapReferenceMapper[it]?.map ?: error("Entity ${idMapper[it]?.id} has no map reference")
            map to map.dimensions.of(position)
        }
        for ((mapAndChunkPos, chunkEntities) in entitiesByChunk) {
            saveEntitiesAt(chunkEntities, mapAndChunkPos.first, mapAndChunkPos.second)
        }
    }

    private fun saveEntitiesAt(entities: List<Entity>, map: EiradirMap, chunkPos: ChunkDimensions) {
        return entitySerialization.saveEntities(entities, getEntitiesFile(map, chunkPos))
    }

    fun loadEntities(map: EiradirMap, chunkPos: ChunkDimensions, processor: (Entity) -> Unit): Collection<Entity> {
        return entitySerialization.loadEntities(getEntitiesFile(map, chunkPos), processor)
    }

}