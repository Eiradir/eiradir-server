package net.eiradir.server.map

import com.badlogic.ashley.core.Entity
import com.fasterxml.jackson.annotation.JsonIgnore
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.IdComponent
import java.util.UUID

class EntityRemovalList {

    private val idMapper = mapperFor<IdComponent>()

    val removedEntities = mutableSetOf<UUID>()

    @get:JsonIgnore
    var isDirty = false

    fun removeEntity(entity: Entity) {
        val id = idMapper[entity]?.id ?: throw IllegalArgumentException("Entity does not have an ID")
        removedEntities.add(id)
        isDirty = true
    }

    fun restoreEntity(entity: Entity) {
        val id = idMapper[entity]?.id ?: throw IllegalArgumentException("Entity does not have an ID")
        restoreEntity(id)
    }

    fun restoreEntity(id: UUID) {
        removedEntities.remove(id)
        isDirty = true
    }

    fun isEntityRemoved(entity: Entity): Boolean {
        val id = idMapper[entity]?.id ?: throw IllegalArgumentException("Entity does not have an ID")
        return isEntityRemoved(id)
    }

    fun isEntityRemoved(id: UUID): Boolean {
        return removedEntities.contains(id)
    }
}