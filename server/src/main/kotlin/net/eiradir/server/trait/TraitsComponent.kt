package net.eiradir.server.trait

import com.badlogic.ashley.core.Component

class TraitsComponent : Component {
    val traits = mutableListOf<TraitInstance>()
}