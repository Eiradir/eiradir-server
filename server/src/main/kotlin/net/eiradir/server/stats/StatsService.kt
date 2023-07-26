package net.eiradir.server.stats

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.data.StatType
import net.eiradir.server.registry.Registries
import net.eiradir.server.stats.entity.BuffedComponent
import net.eiradir.server.stats.entity.StatsComponent
import net.eiradir.server.stats.event.BuffAppliedEvent
import net.eiradir.server.stats.event.BuffRemovedEvent

class StatsService(private val registries: Registries, private val eventBus: EventBus) {
    private val statsMapper = mapperFor<StatsComponent>()
    private val buffedMapper = mapperFor<BuffedComponent>()

    fun getEffectiveStat(entity: Entity, statType: StatType, statsComponent: StatsComponent? = statsMapper[entity]): Int {
        if (statsComponent == null) {
            return statType.default(entity)
        }

        var value = statsComponent.statValues[statType] ?: statType.default(entity)
        for (buffInstance in statsComponent.statBuffs[statType]) {
            value = buffInstance.applyBuff(value)
        }
        for (tag in statType.tags) {
            for (buffInstance in statsComponent.statTagBuffs[tag]) {
                value = buffInstance.applyBuff(value)
            }
        }
        return value
    }

    fun addBuff(entity: Entity, buff: BuffInstance, statsComponent: StatsComponent? = statsMapper[entity]) {
        val component = statsComponent ?: StatsComponent().also(entity::add)
        if (!buffedMapper.has(entity)) {
            entity.add(BuffedComponent())
        }
        when (buff) {
            is StatBuffInstance -> {
                component.statBuffs.put(buff.statType, buff)
            }

            is StatTagBuffInstance -> {
                component.statTagBuffs.put(buff.statTag, buff)
            }
        }
        eventBus.post(BuffAppliedEvent(entity, buff))
    }

    fun removeBuff(entity: Entity, buff: BuffInstance, statsComponent: StatsComponent? = statsMapper[entity]) {
        val component = statsComponent ?: return
        entity.remove(BuffedComponent::class.java)
        when (buff) {
            is StatBuffInstance -> {
                component.statBuffs.remove(buff.statType, buff)
            }

            is StatTagBuffInstance -> {
                component.statTagBuffs.remove(buff.statTag, buff)
            }
        }
        eventBus.post(BuffRemovedEvent(entity, buff))
    }

    fun queryStatsByTag(
        entity: Entity,
        tag: String,
        condition: (StatType, Int) -> Boolean,
        statsComponent: StatsComponent? = statsMapper[entity]
    ): Map<StatType, Int> {
        val component = statsComponent ?: StatsComponent().also(entity::add)
        val stats = registries.stats.getByTag(tag)
        val result = mutableMapOf<StatType, Int>()
        for (stat in stats) {
            val value = getEffectiveStat(entity, stat, component)
            if (condition(stat, value)) {
                result[stat] = value
            }
        }
        return result
    }

}