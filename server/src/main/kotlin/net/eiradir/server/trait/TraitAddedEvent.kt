package net.eiradir.server.trait

import com.badlogic.ashley.core.Entity

data class TraitAddedEvent(val entity: Entity, val trait: TraitInstance)
