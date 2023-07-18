package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.extensions.CloneableComponent
import net.eiradir.server.map.EiradirMap

class MapReference : Component, CloneableComponent {
    var map: EiradirMap? = null

    override fun toString(): String {
        return "MapReference(map=$map)"
    }

    override fun copy(): Component {
        return MapReference().also {
            it.map = map
        }
    }
}