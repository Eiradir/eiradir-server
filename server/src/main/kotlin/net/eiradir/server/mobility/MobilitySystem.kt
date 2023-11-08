package net.eiradir.server.mobility

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class MobilitySystem(private val mobilityService: MobilityService) : IteratingSystem(allOf(Mobility::class).get()) {

    private val mobilityMapper = mapperFor<Mobility>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val mobility = mobilityMapper[entity]
        if (mobility.currentMove == null) {
            val nextInput = mobility.moveQueue.removeFirstOrNull()
            nextInput?.let {
                mobility.currentMove = mobilityService.move(entity, it.position, mobility).orNull()
            }
        }
        mobility.currentMove?.let {
            it.timePassed += deltaTime
            if (it.timePassed >= it.duration) {
                mobility.currentMove = null
            }
        }
    }
}