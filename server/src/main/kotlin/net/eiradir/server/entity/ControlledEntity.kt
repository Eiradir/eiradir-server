package net.eiradir.server.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

class ControlledEntity : Component {
    var lastControlledEntity: Entity? = null
    var controlledEntity: Entity? = null
}