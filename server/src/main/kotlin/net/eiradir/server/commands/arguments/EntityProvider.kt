package net.eiradir.server.commands.arguments

import com.badlogic.ashley.core.Entity
import com.mojang.brigadier.context.CommandContext
import net.eiradir.server.commands.CommandSource
import java.util.*

sealed interface EntityProvider {
    fun get(context: CommandContext<CommandSource>): Entity?
    fun toString(context: CommandContext<CommandSource>): String

    class ByEntityId(val entityId: UUID) : EntityProvider {
        override fun get(context: CommandContext<CommandSource>): Entity? {
            val mapView = context.source.mapView
            return mapView.getEntityById(entityId)
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return entityId.toString()
        }
    }

    object ByCursorPosition : EntityProvider {
        override fun get(context: CommandContext<CommandSource>): Entity? {
            val mapView = context.source.mapView
            val entities = mapView.getEntitiesAt(context.source.cursorPosition)
            return entities.firstOrNull()
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return "at cursor position ${context.source.cursorPosition}"
        }
    }

    object BySelection : EntityProvider {
        override fun get(context: CommandContext<CommandSource>): Entity? {
            val mapView = context.source.mapView
            val id = context.source.selectedEntityId ?: return null
            return mapView.getEntityById(id)
        }

        override fun toString(context: CommandContext<CommandSource>): String {
            return "by selection (${context.source.selectedEntityId})"
        }
    }
}