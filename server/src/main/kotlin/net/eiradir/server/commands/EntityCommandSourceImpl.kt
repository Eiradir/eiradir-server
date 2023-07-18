package net.eiradir.server.commands

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import ktx.ashley.mapperFor
import net.eiradir.server.entity.components.GridTransform
import net.eiradir.server.entity.components.IdComponent
import net.eiradir.server.entity.components.MapViewComponent
import net.eiradir.server.extensions.floorToIntVector
import net.eiradir.server.extensions.logger
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import java.util.*

class EntityCommandSourceImpl(override val entity: Entity, private val fallbackMapView: MapView) : EntityCommandSource {

    private val log = logger()

    private val idMapper = mapperFor<IdComponent>()
    private val transformMapper = mapperFor<net.eiradir.server.entity.components.GridTransform>()
    private val mapViewMapper = mapperFor<MapViewComponent>()

    override val name: String get() = idMapper[entity]?.id?.toString() ?: "<entity without id>"
    override val mapView: MapView get() = mapViewMapper[entity]?.mapView ?: fallbackMapView
    override val position: Vector3Int get() = transformMapper[entity]?.position ?: Vector3Int.Zero
    override val cursorPosition: Vector3Int = Vector3Int.Zero
    override val selectedEntityId: UUID? = null

    override fun respond(message: String) {
        log.info("$name: $message")
    }
}
