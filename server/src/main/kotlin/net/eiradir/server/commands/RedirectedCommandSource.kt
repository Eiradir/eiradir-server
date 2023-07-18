package net.eiradir.server.commands

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import net.eiradir.server.commands.CommandSource
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.network.ServerNetworkContext
import java.util.*

class RedirectedCommandSource(private val owner: CommandSource, private val redirect: CommandSource) : CommandSource, EntityCommandSource, ClientCommandSource {
    override val name: String get() = redirect.name
    override val entity: Entity? get() = (redirect as? EntityCommandSource)?.entity
    override val client: ServerNetworkContext? get() = (redirect as? ClientCommandSource)?.client
    override val mapView: MapView get() = redirect.mapView
    override val position: Vector3Int get() = redirect.position
    override val cursorPosition: Vector3Int get() = owner.cursorPosition
    override val selectedEntityId: UUID? get() = owner.selectedEntityId

    override fun respond(message: String) {
        owner.respond(message)
    }
}