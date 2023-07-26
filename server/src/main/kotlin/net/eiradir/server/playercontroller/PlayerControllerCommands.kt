package net.eiradir.server.playercontroller

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.arguments.EntityArgument
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.camera.CameraService
import net.eiradir.server.commands.*

class PlayerControllerCommands(dispatcher: CommandDispatcher<CommandSource>, playerControllerService: PlayerControllerService, cameraService: CameraService) :
    Initializer {
    init {
        dispatcher.register(literal("control").executes {
            val connection = it.clientEntity() ?: return@executes 0
            val lastControlledEntity = playerControllerService.getLastControlledEntity(connection) ?: it.client()?.loadedEntity ?: return@executes 0
            playerControllerService.setControlledEntity(connection, lastControlledEntity)
            cameraService.setCameraTarget(connection, lastControlledEntity)
            return@executes 1
        }.then(argument("entity", EntityArgument.entity()).executes {
            val connection = it.clientEntity() ?: return@executes 0
            val target = EntityArgument.getEntity(it, "entity") ?: return@executes 0
            playerControllerService.setControlledEntity(connection, target)
            cameraService.setCameraTarget(connection, target)
            return@executes 1
        }))

        dispatcher.register(literal("release").executes {
            val connection = it.clientEntity() ?: return@executes 0
            playerControllerService.resetControlledEntity(connection)
            cameraService.resetCameraTarget(connection)
            return@executes 1
        })
    }
}
