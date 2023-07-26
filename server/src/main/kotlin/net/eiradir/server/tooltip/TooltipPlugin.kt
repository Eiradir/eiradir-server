package net.eiradir.server.tooltip

import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.tooltip.network.TooltipPackets
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class TooltipPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::TooltipPackets) bind Initializer::class
    }
}