package net.eiradir.server.mobility

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MobilityPlugin : EiradirPlugin {
    override fun provide(): Module = module {
        singleOf(::MobilitySystem) bind EntitySystem::class
        singleOf(::MobilityService)
    }
}