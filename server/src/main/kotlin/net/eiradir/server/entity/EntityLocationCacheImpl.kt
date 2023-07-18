package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.entity.event.*
import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.plugin.EventBusSubscriber

class EntityLocationCacheImpl : EntityLocationCache, EventBusSubscriber {

    data class MapChunkReference(val mapName: String, val chunkPos: ChunkDimensions)
    data class MapPosReference(val mapName: String, val pos: Vector3Int)

    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val mapReferenceMapper = mapperFor<net.eiradir.server.entity.components.MapReference>()

    private val entitiesByMapChunk: Multimap<MapChunkReference, Entity> = ArrayListMultimap.create()
    private val entitiesByMapPos: Multimap<MapPosReference, Entity> = ArrayListMultimap.create()

    override fun getEntitiesIn(map: EiradirMap, chunkPos: ChunkDimensions): Collection<Entity> {
        val chunkRef = MapChunkReference(map.name, chunkPos)
        return entitiesByMapChunk[chunkRef]
    }

    override fun getEntitiesAt(map: EiradirMap, pos: Vector3Int): Collection<Entity> {
        val posRef = MapPosReference(map.name, pos)
        return entitiesByMapPos[posRef]
    }

    override fun getEntitiesInRange(map: EiradirMap, pos: Vector3Int, range: Int): Collection<Entity> {
        val minChunkPos = map.dimensions.of(pos.x - range, pos.y - range, pos.level - range)
        val maxChunkPos = map.dimensions.of(pos.x + range, pos.y + range, pos.level + range)
        val result = mutableListOf<Entity>()
        val rangeSqr = range * range
        for (chunkX in minChunkPos.x..maxChunkPos.x) {
            for (chunkY in minChunkPos.y..maxChunkPos.y) {
                for (level in minChunkPos.level..maxChunkPos.level) {
                    val chunkPos = map.dimensions.of(chunkX, chunkY, level)
                    val candidates = getEntitiesIn(map, chunkPos)
                    for (candidate in candidates) {
                        val transform = transformMapper[candidate] ?: continue
                        if (transform.position.dst2(pos) <= rangeSqr) {
                            result.add(candidate)
                        }
                    }
                }
            }
        }
        return result
    }

    private fun add(entity: Entity) {
        val mapReference = mapReferenceMapper[entity] ?: throw IllegalStateException("Entity $entity has no MapReference component")
        val transform = transformMapper[entity] ?: throw IllegalStateException("Entity $entity has no Transform component")
        val map = mapReference.map ?: return
        val position = transform.position
        val chunkPos = map.dimensions.of(position)
        entitiesByMapChunk.put(MapChunkReference(map.name, chunkPos), entity)
        entitiesByMapPos.put(MapPosReference(map.name, position), entity)
    }

    private fun remove(entity: Entity) {
        val mapReference = mapReferenceMapper[entity] ?: throw IllegalStateException("Entity $entity has no MapReference component")
        val transform = transformMapper[entity] ?: throw IllegalStateException("Entity $entity has no Transform component")
        val map = mapReference.map ?: return
        val position = transform.position
        val chunkPos = map.dimensions.of(position)
        entitiesByMapChunk.remove(MapChunkReference(map.name, chunkPos), entity)
        entitiesByMapPos.remove(MapPosReference(map.name, position), entity)
    }

    @Subscribe
    fun onEntityAdded(event: EntityAddedEvent) {
        add(event.entity)
    }

    @Subscribe
    fun onEntityRemoved(event: EntityRemovedEvent) {
        remove(event.entity)
    }

    @Subscribe
    fun onEntitiesAdded(event: EntitiesAddedEvent) {
        event.entities.forEach { add(it) }
    }

    @Subscribe
    fun onEntitiesRemoved(event: EntitiesRemovedEvent) {
        event.entities.forEach { remove(it) }
    }

    @Subscribe
    fun onEntityPositionChanged(event: EntityPositionChangedEvent) {
        val map = mapReferenceMapper[event.entity]?.map ?: return
        val oldPosition = event.oldPosition
        val newPosition = event.position
        if (oldPosition != newPosition) {
            entitiesByMapPos.remove(MapPosReference(map.name, oldPosition), event.entity)
            entitiesByMapPos.put(MapPosReference(map.name, newPosition), event.entity)

            val oldChunkPos = map.dimensions.of(oldPosition)
            val newChunkPos = map.dimensions.of(newPosition)
            if (oldChunkPos != newChunkPos) {
                entitiesByMapChunk.remove(MapChunkReference(map.name, oldChunkPos), event.entity)
                entitiesByMapChunk.put(MapChunkReference(map.name, newChunkPos), event.entity)
            }
        }
    }

    @Subscribe
    fun onEntitySwitchedMap(event: EntitySwitchedMapEvent) {
        val entity = event.entity
        val oldMap = event.oldMap
        val newMap = event.newMap
        val transform = transformMapper[entity] ?: throw IllegalArgumentException("Entity does not have a Transform component")
        entitiesByMapChunk.remove(MapChunkReference(oldMap.name, oldMap.dimensions.of(transform.position)), entity)
        entitiesByMapChunk.put(MapChunkReference(newMap.name, newMap.dimensions.of(transform.position)), entity)

        val gridPosition = transform.position
        entitiesByMapPos.remove(MapPosReference(oldMap.name, gridPosition), entity)
        entitiesByMapPos.put(MapPosReference(newMap.name, gridPosition), entity)
    }

    override fun entityChangedPosition(entity: Entity, oldPos: Vector3Int, newPos: Vector3Int) {
        val mapReference = mapReferenceMapper[entity] ?: throw IllegalArgumentException("Entity does not have a MapReference component")
        val map = mapReference.map ?: return

        entitiesByMapChunk.remove(MapChunkReference(map.name, map.dimensions.of(oldPos)), entity)
        entitiesByMapChunk.put(MapChunkReference(map.name, map.dimensions.of(newPos)), entity)

        entitiesByMapPos.remove(MapPosReference(map.name, oldPos), entity)
        entitiesByMapPos.put(MapPosReference(map.name, newPos), entity)
    }
}