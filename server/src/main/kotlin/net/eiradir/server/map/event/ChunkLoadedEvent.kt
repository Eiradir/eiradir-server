package net.eiradir.server.map.event

import net.eiradir.server.map.ChunkDimensions
import net.eiradir.server.map.EiradirMap
import net.eiradir.server.map.view.MapView

data class ChunkLoadedEvent(val mapView: MapView, val map: EiradirMap, val dimensions: ChunkDimensions)