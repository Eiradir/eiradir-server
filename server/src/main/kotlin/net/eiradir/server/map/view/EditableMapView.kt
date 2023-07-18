package net.eiradir.server.map.view

import net.eiradir.server.map.EiradirMap
import net.eiradir.server.math.Vector3Int

interface EditableMapView {
    var editingMap: String
    fun getActionableMap(position: Vector3Int): EiradirMap?
}