package net.eiradir.server.data

import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry


data class Food(
    override val name: String,
    val foodPoints: Int,
    val drinkPoints: Int,
    val restItem: ItemReference?,
    val poison: Int
): RegistryEntry<Food> {
    override fun registry(registries: Registries): Registry<Food> {
        return registries.foods
    }
}