package net.eiradir.server.stats

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EiradirServerPlugin
import net.eiradir.server.stats.buff.BuffSystem
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class StatsPlugin : EiradirServerPlugin {
    override fun provide() = module {
        singleOf(::BuffSystem) bind EntitySystem::class
        singleOf(::StatsService)
    }
}