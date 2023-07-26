package net.eiradir.server.process

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EiradirPlugin
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.process.registry.ProcessRegistry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ProcessPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::ProcessSystem) bind EntitySystem::class
        singleOf(::ProcessCommands) bind Initializer::class
        singleOf(::ProcessService)
        singleOf(::ProcessRegistry)
    }
}