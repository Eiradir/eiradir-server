package net.eiradir.server.camera

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.commands.argument
import net.eiradir.server.commands.arguments.EntityArgument
import net.eiradir.server.commands.arguments.Vector3IntArgument
import net.eiradir.server.commands.literal
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.commands.clientEntity
import net.eiradir.server.playercontroller.PlayerControllerService

class CameraCommands(
    dispatcher: CommandDispatcher<CommandSource>,
    cameraService: CameraService,
    playerControllerService: PlayerControllerService
) : Initializer {

    init {
        dispatcher.register(
            literal("camera").then(
                literal("set").then(
                    argument("pos", Vector3IntArgument.vector3Int()).executes {
                        val connection = it.clientEntity() ?: return@executes 0
                        val pos = Vector3IntArgument.getVector3Int(it, "pos")
                        cameraService.setCameraPosition(connection, pos)
                        it.source.respond("Camera position set to $pos")
                        1
                    })
            ).then(
                literal("get").executes {
                    val connection = it.clientEntity() ?: return@executes 0
                    val position = cameraService.getCameraPosition(connection)
                    it.source.respond("Camera Position: $position")
                    1
                }
            ).then(
                literal("release").executes {
                    val connection = it.clientEntity() ?: return@executes 0
                    cameraService.resetCameraTarget(connection)
                    it.source.respond("Camera no longer following")
                    1
                }
            ).then(
                literal("follow").executes {
                    val connection = it.clientEntity() ?: return@executes 0
                    val target = playerControllerService.getControlledEntity(connection) ?: return@executes 0
                    cameraService.setCameraTarget(connection, target)
                    it.source.respond("Camera now following")
                    1
                }.then(argument("entity", EntityArgument.entity()).executes {
                    val connection = it.clientEntity() ?: return@executes 0
                    val target = EntityArgument.getEntity(it, "entity") ?: return@executes 0
                    cameraService.setCameraTarget(connection, target)
                    return@executes 1
                })
            )
        )
    }

}
