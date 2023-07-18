package net.eiradir.server.persistence

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.*
import net.eiradir.server.extensions.toIntRGBA
import net.eiradir.server.player.GameCharacter
import net.eiradir.server.player.TraitInstanceData
import net.eiradir.server.process.ProcessComponent
import net.eiradir.server.process.persistence.ProcessInstanceData
import net.eiradir.server.registry.Registries
import net.eiradir.server.stats.StatsComponent
import net.eiradir.server.trait.TraitsComponent

class DatabasePersistenceSystem(private val characterStorage: CharacterStorage, private val registries: Registries) : IteratingSystem(family), EntityListener {

    private val databasePersistenceMapper = mapperFor<DatabasePersistenceComponent>()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
    }

    override fun entityAdded(entity: Entity) {
        databasePersistenceMapper[entity].let { component ->
            characterStorage.lock(component.charId)
        }
    }

    override fun entityRemoved(entity: Entity) {
        databasePersistenceMapper[entity].let { component ->
            component.charId = characterStorage.save(characterStorage.persist(entity))
            characterStorage.unlock(component.charId)
        }
    }

    companion object {
        private val family = allOf(DatabasePersistenceComponent::class).get()
    }
}