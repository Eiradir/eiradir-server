package net.eiradir.server.network

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import net.eiradir.server.session.network.ConnectionStatusPacket

class ClientSystem : IteratingSystem(allOf(ClientComponent::class).get()) {
    private val clientMapper = mapperFor<ClientComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val client = clientMapper[entity] ?: return
        tryAdvanceConnectionStatus(entity, client)
    }

    private fun tryAdvanceConnectionStatus(entity: Entity, client: ClientComponent) {
        if (client.status == ConnectionStatus.LOADING) {
            for (component in entity.components) {
                if (component is ConnectionInitializer && !component.isReady) {
                    return
                }
            }

            client.status = ConnectionStatus.COMPLETE
            client.client.send(ConnectionStatusPacket(client.status))
        }
    }
}