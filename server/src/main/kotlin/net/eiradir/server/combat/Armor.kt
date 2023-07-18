package net.eiradir.server.combat

import net.eiradir.server.registry.Registries
import net.eiradir.server.registry.Registry
import net.eiradir.server.registry.RegistryEntry

data class Armor(
    override val name: String,
    val meleeAccuracyPenalty: Int,
    val rangedAccuracyPenalty: Int,
    val dodgePenalty: Int,
    val attackSpeedPenalty: Int,
    val movementSpeedPenalty: Int,
    val parryBonus: Int,
    val spellCastingModifier: Int,
    val slashingResistance: Int,
    val piercingResistance: Int,
    val crushingResistance: Int,
    val coldResistance: Int,
    val fireResistance: Int,
    val breakingSpeed: Int
) : RegistryEntry<Armor> {
    override fun registry(registries: Registries): Registry<Armor> {
        return registries.armors
    }
}