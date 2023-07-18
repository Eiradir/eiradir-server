package net.eiradir.server.combat

import net.eiradir.server.data.ItemReference
import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Weapon(
    override val name: String,
    val type: WeaponType,
    val range: Int,
    val accuracy: Int,
    val criticalChance: Int,
    val breakingSpeed: Int,
    val frontDamage: Int,
    val backDamage: Int,
    val sideDamage: Int,
    val frontDefense: Int,
    val backDefense: Int,
    val sideDefense: Int,
    val attackTime: Int,
    val hitTime: Int,
    val ammunition: ItemReference?,
) : RegistryEntry<Weapon> {
    override fun registry(registries: Registries): Registry<Weapon> {
        return registries.weapons
    }
}