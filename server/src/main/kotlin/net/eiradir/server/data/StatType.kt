package net.eiradir.server.data

import com.badlogic.ashley.core.Entity
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry
import net.eiradir.server.registry.TaggableRegistryEntry

data class StatType(
    override val name: String,
    val iconName: String = name,
    override val tags: Set<String> = emptySet(),
    val default: (Entity) -> Int = { 0 }
) : RegistryEntry<StatType>, TaggableRegistryEntry, IconType {

    override fun registry(registries: Registries): Registry<StatType> {
        return registries.stats
    }

    override fun iconId(registries: Registries): Int {
        return registries.idResolver.resolve("icons", iconName) ?: 0
    }

    companion object {
        val Invalid = StatType("invalid")
    }
}