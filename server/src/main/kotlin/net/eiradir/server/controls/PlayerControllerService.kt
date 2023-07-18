package net.eiradir.server.controls

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.entity.ControlledEntity
import net.eiradir.server.entity.EntityService
import net.eiradir.server.entity.NULL_UUID
import net.eiradir.server.network.ClientComponent

class PlayerControllerService(private val entityService: EntityService, private val eventBus: EventBus) {
    private val clientMapper = mapperFor<ClientComponent>()
    private val controlledEntityMapper = mapperFor<ControlledEntity>()

    fun getLastControlledEntity(clientEntity: Entity, controlledEntityComponent: ControlledEntity? = controlledEntityMapper[clientEntity]): Entity? {
        return controlledEntityComponent?.lastControlledEntity
    }

    fun getControlledEntity(clientEntity: Entity, controlledEntityComponent: ControlledEntity? = controlledEntityMapper[clientEntity]): Entity? {
        return controlledEntityComponent?.controlledEntity
    }

    fun setControlledEntity(clientEntity: Entity, target: Entity, controlledEntityComponent: ControlledEntity? = controlledEntityMapper[clientEntity]) {
        val client = clientMapper[clientEntity]?.client ?: return
        val controlledEntity = controlledEntityComponent ?: ControlledEntity().also(clientEntity::add)
        val previouslyControlled = controlledEntity.controlledEntity
        if (previouslyControlled == target) return
        if (previouslyControlled != null) {
            eventBus.post(PlayerControlReleasedEvent(clientEntity, previouslyControlled))
        }
        controlledEntity.lastControlledEntity = previouslyControlled
        controlledEntity.controlledEntity = target
        eventBus.post(PlayerControlGainedEvent(clientEntity, target))
        client.send(ControllerPacket(entityService.getEntityId(target), ControllerType.Default, 0))
    }

    fun resetControlledEntity(clientEntity: Entity, controlledEntityComponent: ControlledEntity? = controlledEntityMapper[clientEntity]) {
        val client = clientMapper[clientEntity]?.client ?: return
        val previouslyControlled = controlledEntityComponent?.controlledEntity ?: return
        controlledEntityComponent.controlledEntity = null
        eventBus.post(PlayerControlReleasedEvent(clientEntity, previouslyControlled))
        client.send(ControllerPacket(NULL_UUID, ControllerType.None, 0))
    }

}