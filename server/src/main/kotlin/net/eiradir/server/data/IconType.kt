package net.eiradir.server.data

import net.eiradir.server.registry.Registries

interface IconType {
    val name: String
    fun iconId(registries: Registries): Int
}