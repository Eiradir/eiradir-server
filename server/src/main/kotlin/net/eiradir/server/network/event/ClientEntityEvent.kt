package net.eiradir.server.network.event

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.network.entity.ClientComponent
import net.eiradir.server.network.ServerNetworkContext

abstract class ClientEntityEvent(val entity: Entity) {
    val client: ServerNetworkContext? get() = clientMapper[entity]?.client

    companion object {
        val clientMapper = mapperFor<ClientComponent>()
    }
}