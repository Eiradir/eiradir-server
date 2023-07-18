package net.eiradir.server.combat

class ArmorBuilder(private val name: String) {
    var meleeAccuracyPenalty = 0
    var rangedAccuracyPenalty = 0
    var dodgePenalty = 0
    var attackSpeedPenalty = 0
    var movementSpeedPenalty = 0
    var parryBonus = 0
    var spellCastingModifier = 0
    var slashingResistance = 0
    var piercingResistance = 0
    var crushingResistance = 0
    var coldResistance = 0
    var fireResistance = 0
    var breakingSpeed = 0

    fun build(): Armor {
        return Armor(
            name = name,
            meleeAccuracyPenalty = meleeAccuracyPenalty,
            rangedAccuracyPenalty = rangedAccuracyPenalty,
            dodgePenalty = dodgePenalty,
            attackSpeedPenalty = attackSpeedPenalty,
            movementSpeedPenalty = movementSpeedPenalty,
            parryBonus = parryBonus,
            spellCastingModifier = spellCastingModifier,
            slashingResistance = slashingResistance,
            piercingResistance = piercingResistance,
            crushingResistance = crushingResistance,
            coldResistance = coldResistance,
            fireResistance = fireResistance,
            breakingSpeed = breakingSpeed
        )
    }
}