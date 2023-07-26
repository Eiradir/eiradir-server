package net.eiradir.server.menu

import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MenuPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::MenuPackets) bind Initializer::class
    }
}