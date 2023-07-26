package net.eiradir.server.audionce

import com.badlogic.ashley.core.Engine
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.audionce.entity.AudionceComponent
import net.eiradir.server.math.Vector3Int

class AudionceService(val engine: Engine) {

    private val audionceFamily = allOf(AudionceComponent::class).get()
    private val audionceMapper = mapperFor<AudionceComponent>()

    fun getAudionces(position: Vector3Int, multiplier: Float = 1f): List<Audionce> {
        val result = mutableListOf<Audionce>()
        for (entity in engine.getEntitiesFor(audionceFamily)) {
            val component = audionceMapper.get(entity)
            for (audionce in component.audionces) {
                if (audionce.canHear(position, multiplier)) {
                    result.add(audionce)
                }
            }
        }
        return result
    }
}