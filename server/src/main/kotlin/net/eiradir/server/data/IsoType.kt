package net.eiradir.server.data

import net.eiradir.server.registry.Registries

interface IsoType {
    fun isoId(registries: Registries): Int
}