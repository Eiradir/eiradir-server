package net.eiradir.server.entity

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class EntityPlugin : EiradirPlugin {
    override fun provide(): Module = module {
        singleOf(::EntityCommands) bind Initializer::class
        singleOf(::TransformSystem) bind EntitySystem::class
        singleOf(::InventorySystem) bind EntitySystem::class
        singleOf(::AdvancedEncoders)
        singleOf(::EntityService)
    }
}