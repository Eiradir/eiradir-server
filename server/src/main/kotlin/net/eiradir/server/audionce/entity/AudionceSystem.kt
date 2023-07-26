package net.eiradir.server.audionce.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform

class AudionceSystem : IteratingSystem(allOf(AudionceComponent::class).get()) {
    private val audionceMapper = mapperFor<AudionceComponent>()
    private val transformMapper = mapperFor<GridTransform>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = audionceMapper.get(entity)

        for (audionce in component.audionces) {
            audionce.followEntity?.let {
                audionce.position = transformMapper[it].position
            }

            audionce.followCamera?.let {
                audionce.position = it.position
            }
        }
    }

}