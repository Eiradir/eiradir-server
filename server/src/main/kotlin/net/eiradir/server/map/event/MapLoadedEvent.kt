package net.eiradir.server.map.event

import net.eiradir.server.map.EiradirMap
import net.eiradir.server.map.view.MapView

data class MapLoadedEvent(val mapView: MapView, val map: EiradirMap)