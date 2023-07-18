package net.eiradir.server.charcreation

import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.EiradirServerPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CharacterCreationServerPlugin : EiradirServerPlugin {

    override fun provide() = module {
        singleOf(::CharacterCreationRoutes) bind EventBusSubscriber::class
    }

}