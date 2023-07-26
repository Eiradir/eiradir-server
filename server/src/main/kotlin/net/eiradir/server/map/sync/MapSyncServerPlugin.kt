package net.eiradir.server.map.sync

import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class MapSyncServerPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::MapSyncEvents)
    }
}