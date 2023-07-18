package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.event.EntitiesAddedEvent
import net.eiradir.server.entity.event.EntitiesRemovedEvent
import net.eiradir.server.entity.event.EntityAddedEvent
import net.eiradir.server.entity.event.EntityRemovedEvent
import net.eiradir.server.plugin.EventBusSubscriber
import java.util.*

class EntityIdCacheImpl : EntityIdCache, EventBusSubscriber {

    private val idMapper = mapperFor<IdComponent>()
    private val entitiesById = mutableMapOf<UUID, Entity>()

    override fun getEntityById(id: UUID): Entity? {
        return entitiesById[id]
    }

    private fun add(entity: Entity) {
        val entityId = idMapper[entity]?.id ?: throw IllegalStateException("Entity must have an ID")
        entitiesById[entityId] = entity
    }

    private fun remove(entity: Entity) {
        val entityId = idMapper[entity]?.id ?: throw IllegalStateException("Entity must have an ID")
        entitiesById.remove(entityId)
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

}