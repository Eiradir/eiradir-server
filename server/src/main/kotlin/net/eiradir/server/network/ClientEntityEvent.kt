package net.eiradir.server.network

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor

abstract class ClientEntityEvent(val entity: Entity) {
    val client: ServerNetworkContext? get() = clientMapper[entity]?.client

    companion object {
        val clientMapper = mapperFor<ClientComponent>()
    }
}