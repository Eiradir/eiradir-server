package net.eiradir.server.commands

import com.badlogic.gdx.math.Vector3
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import java.util.UUID

interface CommandSource {
    val name: String
    val mapView: MapView
    val position: Vector3Int
    val cursorPosition: Vector3Int
    val selectedEntityId: UUID?
    fun respond(message: String)
}