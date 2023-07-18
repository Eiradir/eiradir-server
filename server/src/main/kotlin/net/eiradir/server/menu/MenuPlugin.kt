package net.eiradir.server.menu

import net.eiradir.server.plugin.EiradirServerPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MenuPlugin : EiradirServerPlugin {
    override fun provide() = module {
        singleOf(::MenuPackets) bind Initializer::class
    }
}