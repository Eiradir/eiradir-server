package net.eiradir.server.plugin

import java.util.*
import java.util.stream.Stream

class SpiPluginLoader<T : EiradirPlugin>(private val pluginClass: Class<T>) : PluginLoader {
    override fun load(): Stream<EiradirPlugin> {
        val commonLoader = ServiceLoader.load(EiradirPlugin::class.java)
        val sidedLoader = ServiceLoader.load(pluginClass)
        return Stream.concat(commonLoader.stream(), sidedLoader.stream()).map { it.get() }
    }
}