package net.eiradir.server.session

import net.eiradir.server.http.KtorInitializer
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.session.network.SessionPackets
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class SessionPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::ServerSessionManagerImpl) bind ServerSessionManager::class
        singleOf(::SessionPackets) bind Initializer::class
        singleOf(::SessionRoutes) bind KtorInitializer::class
    }
}