package net.eiradir.server.session

import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class SessionServerPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::ServerSessionManagerImpl) bind ServerSessionManager::class
        singleOf(::SessionPackets) bind Initializer::class
        singleOf(::SessionRoutes) bind EventBusSubscriber::class
    }
}