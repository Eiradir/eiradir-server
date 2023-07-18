package net.eiradir.server.combat

import net.eiradir.server.data.ItemReference

class WeaponBuilder(private val name: String) {
    var type = WeaponType.UNARMED
    var range = 0
    var accuracy = 0
    var criticalChance = 0
    var breakingSpeed = 0
    var frontDamage = 0
    var backDamage = 0
    var sideDamage = 0
    var frontDefense = 0
    var backDefense = 0
    var sideDefense = 0
    var attackTime = 0
    var hitTime = 0
    var ammunition: ItemReference? = null

    fun build(): Weapon {
        return Weapon(
            name = name,
            type = type,
            range = range,
            accuracy = accuracy,
            criticalChance = criticalChance,
            breakingSpeed = breakingSpeed,
            frontDamage = frontDamage,
            backDamage = backDamage,
            sideDamage = sideDamage,
            frontDefense = frontDefense,
            backDefense = backDefense,
            sideDefense = sideDefense,
            attackTime = attackTime,
            hitTime = hitTime,
            ammunition = ammunition
        )
    }
}