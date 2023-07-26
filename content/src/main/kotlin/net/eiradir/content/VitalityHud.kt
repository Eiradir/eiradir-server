package net.eiradir.content

import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import net.eiradir.server.hud.Hud
import net.eiradir.server.hud.message.NoHudMessages
import net.eiradir.server.services
import net.eiradir.server.stats.entity.StatsComponent

enum class VitalityHudProperties {
    Health,
    MaxHealth,
    Mana,
    MaxMana,
    Hunger,
    MaxHunger
}

class VitalityHud(val target: Entity, private val statTypes: StatTypes) : Hud<VitalityHudProperties, NoHudMessages>() {
    override val propertyKeys = VitalityHudProperties.values()
    override val messageKeys = NoHudMessages.values()
    override val typeName: String get() = "vitality"
    private val statsService = target.services().stats
    private val statsMapper = mapperFor<StatsComponent>()

    private var _statsComponent: StatsComponent? = null
    private val statsComponent: StatsComponent?
        get() {
            if (_statsComponent == null) {
                _statsComponent = statsMapper[target]
            }
            return _statsComponent
        }

    private val health = createIntProperty(VitalityHudProperties.Health).from { statsService.getEffectiveStat(target, statTypes.health, statsComponent) }
    private val maxHealth =
        createIntProperty(VitalityHudProperties.MaxHealth).from { statsService.getEffectiveStat(target, statTypes.maxHealth, statsComponent) }
    private val mana = createIntProperty(VitalityHudProperties.Mana).from { statsService.getEffectiveStat(target, statTypes.mana, statsComponent) }
    private val maxMana = createIntProperty(VitalityHudProperties.MaxMana).from { statsService.getEffectiveStat(target, statTypes.maxMana, statsComponent) }
    private val hunger = createIntProperty(VitalityHudProperties.Hunger).from { statsService.getEffectiveStat(target, statTypes.hunger, statsComponent) }
    private val maxHunger =
        createIntProperty(VitalityHudProperties.MaxHunger).from { statsService.getEffectiveStat(target, statTypes.maxHunger, statsComponent) }
}


