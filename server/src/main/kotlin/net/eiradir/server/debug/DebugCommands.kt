package net.eiradir.server.debug

import com.mojang.brigadier.CommandDispatcher
import net.eiradir.server.map.MapManager
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.camera.CameraService
import net.eiradir.server.commands.*
import net.eiradir.server.entity.EntityService
import net.eiradir.server.network.NetworkServer

class DebugCommands(
    dispatcher: CommandDispatcher<CommandSource>,
    mapManager: MapManager,
    networkServer: NetworkServer,
    cameraService: CameraService,
    entityService: EntityService
) : Initializer {
    init {
        dispatcher.register(
            literal("position").executes {
                val position = it.source.position
                it.source.respond("Position: $position")
                1
            }
        )

        dispatcher.register(
            literal("id").executes {
                val targetEntity = it.controlledEntity() ?: return@executes 0
                it.source.respond("Controlled Entity ID: ${entityService.getEntityId(targetEntity)}")
                1
            }
        )

        dispatcher.register(
            literal("debug").then(
                literal("client").then(
                    argument("client", ClientArgument.client(networkServer)).executes {
                        val client = ClientArgument.getClient(it, "client")
                        it.source.respond("Client: ${client.session?.username} (${client.address})")
                        val entity = client.connectionEntity
                        if (entity != null) {
                            val cameraPosition = cameraService.getCameraPosition(entity)
                            val cameraTarget = cameraService.getCameraTarget(entity)
                            val watchedChunks = cameraService.getWatchedChunks(entity)
                            it.source.respond(" - Camera Position: $cameraPosition")
                            it.source.respond(" - Watched Chunks: ${watchedChunks.size}")
                            if (cameraTarget != null) {
                                val cameraTargetId = entityService.getEntityId(cameraTarget)
                                it.source.respond(" - Camera Target: $cameraTargetId")
                            } else {
                                it.source.respond(" - Camera Target: None")
                            }
                        } else {
                            it.source.respond(" - No entity attached")
                        }
                        1
                    }
                )
            ).then(
                literal("world").executes {
                    it.source.respond("Loaded Chunks: " + mapManager.mergedMap.getLoadedChunks().size)
                    1
                }
            )
        )
    }
}