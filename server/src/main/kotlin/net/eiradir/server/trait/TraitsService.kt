package net.eiradir.server.trait

import com.badlogic.ashley.core.Entity
import com.google.common.eventbus.EventBus
import ktx.ashley.mapperFor
import net.eiradir.server.registry.Registries
import net.eiradir.server.stats.StatsService
import net.eiradir.server.stats.buff.StatBuffInstance
import net.eiradir.server.stats.buff.StatTagBuffInstance

class TraitsService(private val registries: Registries, private val statsService: StatsService, private val eventBus: EventBus) {
    private val traitsMapper = mapperFor<TraitsComponent>()

    fun addTrait(entity: Entity, traitInstance: TraitInstance, traitsComponent: TraitsComponent? = traitsMapper[entity]) {
        val component = traitsComponent ?: TraitsComponent().also(entity::add)
        val shouldBuffsBeDisabled = !traitInstance.trait.stackBuffs && component.traits.any { it.trait == traitInstance.trait }
        for (providedBuff in traitInstance.trait.providesBuffs) {
            if (providedBuff.first.startsWith("#")) {
                val buffInstance = StatTagBuffInstance(providedBuff.second, providedBuff.first.substring(1))
                buffInstance.enabled = !shouldBuffsBeDisabled
                statsService.addBuff(entity, buffInstance)
                traitInstance.providedBuffs.add(buffInstance)
            } else {
                val statType = registries.stats.getByName(providedBuff.first) ?: continue
                val buffInstance = StatBuffInstance(providedBuff.second, statType)
                buffInstance.enabled = !shouldBuffsBeDisabled
                statsService.addBuff(entity, buffInstance)
                traitInstance.providedBuffs.add(buffInstance)
            }
        }
        component.traits.add(traitInstance)
        eventBus.post(TraitAddedEvent(entity, traitInstance))

        for (it in traitInstance.trait.providesTraits) {
            val subTrait = registries.traits.getByName(it) ?: continue
            val subTraitInstance = TraitInstance(subTrait, emptyMap(), true)
            addTrait(entity, subTraitInstance, component)
            traitInstance.providedTraits.add(subTraitInstance)
        }
    }

    fun removeTrait(entity: Entity, trait: TraitInstance, traitsComponent: TraitsComponent? = traitsMapper[entity]) {
        traitsComponent ?: return

        for (providedBuff in trait.providedBuffs) {
            statsService.removeBuff(entity, providedBuff)
        }
        traitsComponent.traits.remove(trait)

        if (!trait.trait.stackBuffs) {
            val firstLeftoverTrait = traitsComponent.traits.firstOrNull()
            firstLeftoverTrait?.providedBuffs?.forEach { it.enabled = true }
        }

        for (it in trait.providedTraits) {
            removeTrait(entity, it, traitsComponent)
        }

        eventBus.post(TraitRemovedEvent(entity, trait))
    }

    fun removeTraitByType(entity: Entity, trait: Trait, traitsComponent: TraitsComponent? = traitsMapper[entity]) {
        traitsComponent ?: return

        val traitInstance = traitsComponent.traits.firstOrNull { it.trait == trait } ?: return
        removeTrait(entity, traitInstance, traitsComponent)
    }
}