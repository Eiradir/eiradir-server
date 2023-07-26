package net.eiradir.server.stats.entity

import com.badlogic.ashley.core.Component
import com.google.common.collect.ArrayListMultimap
import net.eiradir.server.data.StatType
import net.eiradir.server.stats.BuffInstance

class StatsComponent : Component {
    val statValues = mutableMapOf<StatType, Int>()
    val statBuffs = ArrayListMultimap.create<StatType, BuffInstance>()
    val statTagBuffs = ArrayListMultimap.create<String, BuffInstance>()
}