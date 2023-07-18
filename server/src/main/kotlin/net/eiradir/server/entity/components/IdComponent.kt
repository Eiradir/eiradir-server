package net.eiradir.server.entity.components

import com.badlogic.ashley.core.Component
import net.eiradir.server.extensions.CloneableComponent
import java.util.UUID

data class IdComponent(val id: UUID) : Component, CloneableComponent {
    override fun copy(): Component {
        return net.eiradir.server.entity.components.IdComponent(id)
    }
}