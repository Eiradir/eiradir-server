package net.eiradir.server.console

import net.eiradir.server.map.*
import net.eiradir.server.map.view.EditableMapView
import net.eiradir.server.map.view.MapView
import net.eiradir.server.math.Vector3Int
import net.eiradir.server.map.MapManager

class ServerConsoleMapView(private val mapManager: MapManager) : MapView by mapManager, EditableMapView {
    override var editingMap: String = MapManager.MERGED

    override fun getActionableMap(position: Vector3Int): EiradirMap? {
        return when (editingMap) {
            MapManager.MERGED -> mapManager.getActionableMapFromMerged(position)
            else -> mapManager.getLoadedMapByName(editingMap)
        }
    }
}