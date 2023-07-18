package net.eiradir.server.commands

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.console.CommandResponsePacket
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.map.view.MapView
import net.eiradir.server.entity.components.MapViewComponent
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.entity.ControlledEntity
import net.eiradir.server.network.ServerNetworkContext
import java.util.UUID

class NetworkClientCommandSource(override val client: ServerNetworkContext, private val fallbackMapView: MapView) : EntityCommandSource, ClientCommandSource {

    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val controlledEntity = mapperFor<ControlledEntity>()
    private val mapViewMapper = mapperFor<MapViewComponent>()

    override val entity: Entity? get() = client.connectionEntity?.let { controlledEntity[it]?.controlledEntity } ?: client.connectionEntity
    override val name: String get() = client.session?.username ?: "unknown network user"
    override val mapView: MapView get() = client.connectionEntity?.let { mapViewMapper[it] }?.mapView ?: fallbackMapView
    override val position: Vector3Int = entity?.let { transformMapper[it]?.position } ?: Vector3Int.Zero
    override var cursorPosition: Vector3Int = Vector3Int.Zero
    override var selectedEntityId: UUID? = null

    override fun respond(message: String) {
        client.send(CommandResponsePacket(message))
    }
}