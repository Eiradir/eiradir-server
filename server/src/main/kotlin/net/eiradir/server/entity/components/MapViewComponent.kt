package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.map.view.MapView

data class MapViewComponent(val mapView: MapView) : Component