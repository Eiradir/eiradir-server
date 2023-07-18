package net.eiradir.server.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.google.common.eventbus.EventBus
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.entity.components.MapReference
import net.eiradir.server.entity.event.EntityDirectionChangedEvent
import net.eiradir.server.entity.event.EntityPositionChangedEvent

class TransformSystem(private val eventBus: EventBus) : IteratingSystem(allOf(GridTransform::class, MapReference::class).get()) {

    private val transformMapper = mapperFor<GridTransform>()
    private val mapReferenceMapper = mapperFor<MapReference>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = transformMapper[entity] ?: return
        val map = mapReferenceMapper[entity]?.map ?: return
        if(!transform.hasLastPosition) {
            transform.lastPosition = transform.position
            transform.hasLastPosition = true
        }

        if (transform.position != transform.lastPosition) {
            eventBus.post(EntityPositionChangedEvent(entity, map, transform.position, transform.lastPosition))
            transform.lastPosition = transform.position
        }

        if (transform.direction != transform.lastDirection) {
            eventBus.post(EntityDirectionChangedEvent(entity, map, transform.direction, transform.lastDirection))
            transform.lastDirection = transform.direction
        }
    }
}