package net.eiradir.content

import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders
import net.eiradir.server.stats.buff.ConstantBonus
import net.eiradir.server.trait.Trait

class Traits(define: RegistryBuilders) : Initializer {

    val PROFESSION = "profession"
    val SOCIAL = "social"
    val GEOGRAPHIC = "geographic"
    val SPAWN = "spawn"
    val TRAIT = "trait"

    init {
        define.trait("farmer") {
            category = PROFESSION
            availableAtCreation = true
            provide("farming_proficiency")
        }

        define.trait("noble_birth") {
            availableAtCreation = true
            category = SOCIAL
        }

        define.trait("arid_desert") {
            availableAtCreation = true
            category = GEOGRAPHIC
        }

        define.trait("farming_proficiency") {
            category = TRAIT
            availableAtCreation = true
            valence = Trait.Valence.Good
            buff("farming_skill", ConstantBonus(20))
        }

        define.trait("malvior_spawn") {
            availableAtCreation = true
            category = SPAWN
        }

        define.trait("test") {
            provide("test2")
            buff("health", ConstantBonus(2000))
        }

        define.trait("test2") {
            stackBuffs = true
            buff("mana", ConstantBonus(-2000))
        }
    }
}