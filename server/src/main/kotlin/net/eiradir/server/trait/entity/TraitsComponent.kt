package net.eiradir.server.trait.entity

import com.badlogic.ashley.core.Component
import net.eiradir.server.trait.TraitInstance

class TraitsComponent : Component {
    val traits = mutableListOf<TraitInstance>()
}