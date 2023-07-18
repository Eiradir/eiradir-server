package net.eiradir.server.interact

import net.eiradir.server.plugin.EiradirServerPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class InteractionPlugin : EiradirServerPlugin {
    override fun provide() = module {
        singleOf(::InteractionPackets) bind Initializer::class
        singleOf(::InteractionRegistry)
        singleOf(::InteractionService)
        singleOf(::InteractableRegistry)
    }
}