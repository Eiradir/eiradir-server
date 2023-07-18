package net.eiradir.server.registry

import java.io.Reader

class StaticIdMappingsResolver : IdResolver {

    data class RegistryId(val registry: String, val id: Int)
    data class RegistryKey(val registry: String, val key: String)

    private val idToKeyMappings = mutableMapOf<RegistryId, String>()
    private val keyToIdMappings = mutableMapOf<RegistryKey, Int>()

    fun loadFromResources(path: String = "/id_mappings.ini"): StaticIdMappingsResolver {
        IdResolver::class.java.getResourceAsStream(path)?.reader()?.use {
            load(it)
        } ?: throw IllegalStateException("Unable to load $path from resources")
        return this
    }

    fun load(reader: Reader) {
        reader.useLines { lines ->
            var currentGroup = ""
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("[")) {
                    currentGroup = trimmed.substring(1, trimmed.length - 1)
                } else if (trimmed.isNotEmpty()) {
                    val id = trimmed.substringBefore("=").trim()
                    val name = trimmed.substringAfter("=").trim().trim('"')
                    idToKeyMappings[RegistryId(currentGroup, id.toInt())] = name
                    keyToIdMappings[RegistryKey(currentGroup, name)] = id.toInt()
                }
            }
        }
    }

    override fun resolve(registryName: String, name: String): Int? {
        return keyToIdMappings[RegistryKey(registryName, name)]
    }
}