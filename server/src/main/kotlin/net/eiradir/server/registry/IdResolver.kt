package net.eiradir.server.registry

interface IdResolver {
    fun resolve(registryName: String, name: String): Int?
}