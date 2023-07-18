package net.eiradir.server.charselection

import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirServerPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CharacterSelectionServerPlugin : EiradirServerPlugin {

    override fun provide() = module {
        singleOf(::CharacterSelectionRoutes) bind EventBusSubscriber::class
        singleOf(::CharacterSelectionEvents) bind EventBusSubscriber::class
    }

}