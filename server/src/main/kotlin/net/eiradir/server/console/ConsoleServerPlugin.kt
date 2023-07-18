package net.eiradir.server.console

import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.commands.ServerCommandSource
import net.eiradir.server.plugin.EiradirServerPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ConsoleServerPlugin : EiradirServerPlugin {

    override fun provide() = module {
        singleOf(::ServerCommandSourceImpl) bind ServerCommandSource::class
        singleOf(::ConsolePackets) bind Initializer::class
        singleOf(::ConsoleEvents) bind EventBusSubscriber::class
    }
}