package net.eiradir.server.debug

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DebugPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::DebugCommands) bind Initializer::class
    }
}