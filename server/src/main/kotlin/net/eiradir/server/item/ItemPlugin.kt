package net.eiradir.server.item

import net.eiradir.server.plugin.EiradirServerPlugin
import net.eiradir.server.plugin.Initializer
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ItemPlugin : EiradirServerPlugin {
    override fun provide() = module {
        singleOf(::ItemCommands) bind Initializer::class
        singleOf(::InventoryService)
        singleOf(::WorldItemOwner) bind Initializer::class
    }
}