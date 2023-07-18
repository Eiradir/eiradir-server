package net.eiradir.content

import com.badlogic.ashley.core.Entity
import net.eiradir.server.plugin.Initializer
import net.eiradir.server.registry.RegistryBuilders

class StatTypes(define: RegistryBuilders) : Initializer {

    val health = define.stat("health", 5000)
    val maxHealth = define.stat("health_max", 10000)
    val hunger = define.stat("hunger", 10000)
    val maxHunger = define.stat("hunger_max", 10000)
    val thirst = define.stat("thirst", 10000)
    val maxThirst = define.stat("thirst_max", 10000)
    val mana = define.stat("mana", 10000)
    val maxMana = define.stat("mana_max", 10000)

    init {
        define.stat("age")
        define.stat("strength", 10)
        define.stat("dexterity", 10)
        define.stat("constitution", 10)
        define.stat("perception", 10)
        define.stat("agility", 10)
        define.stat("intelligence", 10)
        define.stat("arcanum", 10)

        fun manaCost(school: String, base: Int): (Entity) -> Int {
            return {_: Entity -> 300 }
        }

        define.stat("spell_fireball") {
            tag("spell")
        }
        define.stat("spell_fireball_mana", manaCost("destruction", 300)) {
            tag("spell_mana")
        }
    }
}