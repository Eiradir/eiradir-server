package net.eiradir.server.charselection

import net.eiradir.server.http.KtorInitializer
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CharacterSelectionPlugin : EiradirPlugin {

    override fun provide() = module {
        singleOf(::CharacterSelectionRoutes) bind KtorInitializer::class
        singleOf(::CharacterSelectionEvents) bind EventBusSubscriber::class
    }

}