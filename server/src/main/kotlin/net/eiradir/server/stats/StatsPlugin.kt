package net.eiradir.server.stats

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.stats.entity.BuffSystem
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class StatsPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::BuffSystem) bind EntitySystem::class
        singleOf(::StatsService)
    }
}