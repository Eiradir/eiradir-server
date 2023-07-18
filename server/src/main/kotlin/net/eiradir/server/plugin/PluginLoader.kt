package net.eiradir.server.plugin

import java.util.stream.Stream


interface PluginLoader {
    fun load(): Stream<EiradirPlugin>
}