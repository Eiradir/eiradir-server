package net.eiradir.server.stats.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class BuffSystem : IteratingSystem(allOf(StatsComponent::class, BuffedComponent::class).get()) {
    private val statsMapper = mapperFor<StatsComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val statsComponent = statsMapper.get(entity) ?: return
        for (buff in statsComponent.statBuffs.values()) {
            buff.secondsPassed += deltaTime
        }
        for (buff in statsComponent.statTagBuffs.values()) {
            buff.secondsPassed += deltaTime
        }
    }

}