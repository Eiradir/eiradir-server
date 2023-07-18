package net.eiradir.server.trait

import com.badlogic.ashley.core.Entity

data class TraitRemovedEvent(val entity: Entity, val trait: TraitInstance)
