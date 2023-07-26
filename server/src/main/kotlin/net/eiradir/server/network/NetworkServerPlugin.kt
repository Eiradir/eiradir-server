package net.eiradir.server.network

import com.badlogic.ashley.core.EntitySystem
import com.google.common.eventbus.Subscribe
import ktx.ashley.mapperFor
import net.eiradir.server.network.entity.ClientComponent
import net.eiradir.server.network.entity.ClientSystem
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.session.network.ConnectionStatusPacket
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.session.event.ClientAuthorizedEvent
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class NetworkServerPlugin : EiradirPlugin, EventBusSubscriber {

    private val clientMapper = mapperFor<ClientComponent>()

    @Subscribe
    fun onClientAuthorized(event: ClientAuthorizedEvent) {
        event.client.connectionEntity?.let { clientMapper[it] }?.let {
            it.status = ConnectionStatus.LOADING
            it.client.send(ConnectionStatusPacket(it.status))
        }
    }

    override fun provide() = module {
        singleOf(::ClientSystem) bind EntitySystem::class
    }
}
