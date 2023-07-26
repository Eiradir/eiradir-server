package net.eiradir.server.trait

import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class TraitsPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::TraitCommands) bind Initializer::class
        singleOf(::TraitsService)
        singleOf(::TraitRegistry)
    }
}