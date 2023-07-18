package net.eiradir.server.hud.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class HudSystem : IteratingSystem(allOf(HudComponent::class).get()) {
    private val hudMapper = mapperFor<HudComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val hudComponent = hudMapper[entity] ?: return
        hudComponent.huds.values.forEach { hud ->
            hud.update(deltaTime)
        }
    }
}