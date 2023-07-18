package net.eiradir.server.entity

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.EngineEntity
import ktx.ashley.entity

class EntityCollection(private val engine: Engine) {
    private val entities = mutableListOf<Entity>()

    fun add(body: EngineEntity.() -> Unit): Entity {
        return engine.entity(body).also(entities::add)
    }

    fun add(entity: Entity) {
        engine.addEntity(entity)
        entities.add(entity)
    }

    fun removeAll() {
        entities.forEach { engine.removeEntity(it) }
        entities.clear()
    }

}