package net.eiradir.server.charcreation

import net.eiradir.server.http.KtorInitializer
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CharacterCreationPlugin : EiradirPlugin {

    override fun provide() = module {
        singleOf(::CharacterCreationRoutes) bind KtorInitializer::class
    }

}