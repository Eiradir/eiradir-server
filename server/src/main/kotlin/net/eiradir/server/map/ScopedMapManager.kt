package net.eiradir.server.map

class ScopedMapManager(private val mapManager: MapManager) {

    val scopedMaps = mutableMapOf<String, EiradirMap>()

    fun loadScoped(map: EiradirMap): Boolean {
        if (mapManager.getLoadedMapByName(map.name) == null) {
            scopedMaps[map.name] = map
            return true
        }
        return false
    }

    fun getMapByName(name: String): EiradirMap? {
        return mapManager.getLoadedMapByName(name) ?: scopedMaps[name]
    }

    override fun toString(): String {
        return "ScopedMapManager(scopedMaps=$scopedMaps)"
    }
}