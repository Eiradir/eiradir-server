package net.eiradir.server.trait.event

import com.badlogic.ashley.core.Entity
import net.eiradir.server.trait.TraitInstance

data class TraitRemovedEvent(val entity: Entity, val trait: TraitInstance)
