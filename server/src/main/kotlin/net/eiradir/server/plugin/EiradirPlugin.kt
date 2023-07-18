package net.eiradir.server.plugin

import net.eiradir.server.registry.Registries
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module

interface EiradirPlugin : KoinComponent {
    fun provide(): Module = module {  }
    fun load(registries: Registries) = Unit
}