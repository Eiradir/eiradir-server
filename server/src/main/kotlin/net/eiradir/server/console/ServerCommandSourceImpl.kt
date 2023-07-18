package net.eiradir.server.console

import net.eiradir.server.extensions.logger
import net.eiradir.server.map.MapManager
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.commands.ServerCommandSource
import net.eiradir.server.network.NetworkServer
import java.util.*

class ServerCommandSourceImpl(
    override val networkServer: NetworkServer,
    private val mapManager: MapManager
) : ServerCommandSource {

    private val log = logger()

    override val name: String = "server"
    override val mapView: MapView by lazy {
        ServerConsoleMapView(mapManager)
    }
    override val position: Vector3Int = Vector3Int.Zero
    override val cursorPosition: Vector3Int = Vector3Int.Zero
    override val selectedEntityId: UUID? = null

    override fun respond(message: String) {
        log.info(message)
    }
}