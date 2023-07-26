package net.eiradir.server.camera

import com.badlogic.ashley.core.EntitySystem
import net.eiradir.server.camera.entity.CameraSystem
import net.eiradir.server.camera.network.CameraPackets
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.plugin.EiradirPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class CameraPlugin : EiradirPlugin {
    override fun provide() = module {
        singleOf(::CameraSystem) bind EntitySystem::class
        singleOf(::CameraCommands) bind Initializer::class
        singleOf(::CameraPackets) bind Initializer::class
        singleOf(::CameraService)
    }
}