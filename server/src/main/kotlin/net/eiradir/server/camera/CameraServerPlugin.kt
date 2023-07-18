package net.eiradir.server.camera

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.plugin.EventBusSubscriber
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirServerPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CameraServerPlugin : EiradirServerPlugin {

    override fun provide() = module {
        singleOf(::CameraSystem) bind EntitySystem::class
        singleOf(::CameraCommands) bind Initializer::class
        singleOf(::CameraPackets) bind Initializer::class
        singleOf(::CameraEventHandlers) bind EventBusSubscriber::class
        singleOf(::CameraService)
    }

}