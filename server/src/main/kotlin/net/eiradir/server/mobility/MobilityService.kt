package net.eiradir.server.mobility

import arrow.core.Either
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.math.Vector3Int

class MobilityService {

    private val mobilityMapper = mapperFor<Mobility>()
    private val transformMapper = mapperFor<GridTransform>()

    fun move(entity: Entity, position: Vector3Int, mobilityComponent: Mobility? = mobilityMapper[entity]): Either<MoveError, Move> {
        mobilityComponent ?: return Either.Left(MoveError.Immovable)
        val transform = transformMapper[entity] ?: return Either.Left(MoveError.Immovable)
        transform.direction = transform.position.directionTo(position)
        transform.lastDirection = transform.direction
        transform.position = position
        return Either.Right(Move(position, 0.5f))
    }
}