package net.eiradir.server.plugin

import net.eiradir.server.registry.Registries

interface EiradirServerPlugin : EiradirPlugin {
    override fun load(registries: Registries) = Unit
}