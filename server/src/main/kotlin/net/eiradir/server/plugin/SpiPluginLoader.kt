package net.eiradir.server.plugin

import java.util.*
import java.util.stream.Stream

class SpiPluginLoader<T : EiradirPlugin>(private val pluginClass: Class<T>) : PluginLoader {
    override fun load(): Stream<EiradirPlugin> {
        val loader = ServiceLoader.load(pluginClass)
        return loader.stream().map { it.get() }
    }
}