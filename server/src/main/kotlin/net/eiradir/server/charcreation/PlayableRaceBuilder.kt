package net.eiradir.server.charcreation

import com.badlogic.gdx.graphics.Color
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

class PlayableRaceBuilder(val name: String) {
    var maleRaceName: String = ""
    var femaleRaceName: String = ""
    var minAge: Int = 18
    var maxAge: Int = 99
    val maxStatPoints: Int = 85
    private val minStats: MutableMap<String, Int> = mutableMapOf()
    private val gearOptions: Multimap<String, String> = ArrayListMultimap.create()
    private val vistOptions: Multimap<String, String> = ArrayListMultimap.create()
    private val skinColors: MutableSet<Color> = mutableSetOf()
    private val hairColors: MutableSet<Color> = mutableSetOf()

    fun minStat(stat: String, value: Int): PlayableRaceBuilder {
        minStats[stat] = value
        return this
    }

    fun gear(category: String, item: String): PlayableRaceBuilder {
        gearOptions.put(category, item)
        return this
    }

    fun visualTrait(category: String, vist: String): PlayableRaceBuilder {
        vistOptions.put(category, vist)
        return this
    }

    fun skinColor(color: Color): PlayableRaceBuilder {
        skinColors.add(color)
        return this
    }

    fun hairColor(color: Color): PlayableRaceBuilder {
        hairColors.add(color)
        return this
    }

    fun build(): PlayableRace {
        return PlayableRace(
            name = name,
            maleRaceName = maleRaceName,
            femaleRaceName = femaleRaceName,
            maxStatPoints = maxStatPoints,
            minStats = minStats,
            minAge = minAge,
            maxAge = maxAge,
            gearOptions = gearOptions,
            visualTraitOptions = vistOptions,
            skinColors = skinColors,
            hairColors = hairColors
        )
    }
}